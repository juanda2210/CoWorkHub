package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.component.MonthlyUsageCalculator;
import com.campuslands.coworkhub.business.service.BookingService;
import com.campuslands.coworkhub.business.serviceResult.BookingServiceResult;
import com.campuslands.coworkhub.persistence.entity.*;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import com.campuslands.coworkhub.persistence.repository.AdditionalServiceJpaRepository;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import com.campuslands.coworkhub.persistence.repository.MemberJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final BookingJpaRepository bookings;
    private final MemberJpaRepository members;
    private final RoomJpaRepository rooms;
    private final AdditionalServiceJpaRepository additionalServices;
    private final Clock clock;
    private final MonthlyUsageCalculator usage;

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> create(Long memberId, Long roomId,
                                                      LocalDateTime startTime, LocalDateTime endTime,
                                                      int attendees, Map<Long, Integer> services) {
        Optional<MemberEntity> memberOpt = members.findById(memberId);
        if (memberOpt.isEmpty()) {
            return BookingServiceResult.memberNotFound();
        }
        MemberEntity member = memberOpt.get();
        if (member.getStatus() != MemberStatus.ACTIVE) {
            return BookingServiceResult.memberNotActive();
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        boolean sameDay = startTime.toLocalDate().equals(endTime.toLocalDate());
        if (!sameDay || minutes < 30 || minutes > 480 || minutes % 30 != 0) {
            return BookingServiceResult.invalidDuration();
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (startTime.isBefore(now.plusHours(1))) {
            return BookingServiceResult.tooSoon();
        }

        Optional<RoomEntity> roomOpt = rooms.findWithLockById(roomId);
        if (roomOpt.isEmpty()) {
            return BookingServiceResult.roomNotFound();
        }
        RoomEntity room = roomOpt.get();

        if (!room.isActive()) {
            return BookingServiceResult.roomInactive();
        }

        if (attendees > room.getCapacity()) {
            return BookingServiceResult.capacityExceeded();
        }
        LocationEntity location = room.getLocation();

        if (startTime.toLocalTime().isBefore(location.getOpeningTime())
                || endTime.toLocalTime().isAfter(location.getClosingTime())) {
            return BookingServiceResult.outsideOpeningHours();
        }

        long activeBookings = bookings.countByMemberIdAndStatusInAndStartTimeAfter(
                memberId, ACTIVE_STATUSES, now);
        if (activeBookings >= member.getMembershipPlan().getMaxActiveBookings()) {
            return BookingServiceResult.bookingLimitReached();
        }

        boolean overlaps = bookings
                .findByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(roomId, endTime, startTime)
                .stream()
                .anyMatch(b -> ACTIVE_STATUSES.contains(b.getStatus()));
        if (overlaps) {
            return BookingServiceResult.roomNotAvailable();
        }

        List<BookingItemEntity> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : services.entrySet()) {
            Optional<AdditionalServiceEntity> service = additionalServices.findById(entry.getKey());
            if (service.isEmpty()) {
                return BookingServiceResult.serviceNotFound();
            }
            items.add(new BookingItemEntity(
                    entry.getValue(), service.get().getUnitPrice(), null, service.get()));
        }

        BigDecimal roomCost = calculateRoomCost(member, room, startTime, endTime);
        BigDecimal servicesCost = calculateServicesCost(items);

        BookingEntity booking = new BookingEntity(startTime, endTime, attendees,
                BookingStatus.PENDING, roomCost, servicesCost, roomCost.add(servicesCost), null);
        booking.setMember(member);
        booking.setRoom(room);
        for (BookingItemEntity item : items) {
            item.setBooking(booking);
            booking.getItems().add(item);
        }

        return BookingServiceResult.ok(bookings.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingEntity> findById(Long id) {
        return bookings.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntity> findByMember(Long memberId, Pageable pageable) {
        return bookings.findByMemberId(memberId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntity> findByRoomAndDate(Long roomId, LocalDate date, Pageable pageable) {
        return bookings.findByRoomIdAndStartTimeBetween(
                roomId, date.atStartOfDay(), date.atTime(LocalTime.MAX), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable) {
        return bookings.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> addService(Long bookingId, Long serviceId, int quantity) {
        Optional<BookingEntity> current = bookings.findById(bookingId);
        if (current.isEmpty()) {
            return BookingServiceResult.notFound();
        }
        BookingEntity booking = current.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            return BookingServiceResult.notPending();
        }
        Optional<AdditionalServiceEntity> service = additionalServices.findById(serviceId);
        if (service.isEmpty()) {
            return BookingServiceResult.serviceNotFound();
        }

        Optional<BookingItemEntity> existing = findItem(booking, serviceId);
        if (existing.isPresent()) {
            BookingItemEntity item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            booking.getItems().add(new BookingItemEntity(
                    quantity, service.get().getUnitPrice(), booking, service.get()));
        }

        recalculateCosts(booking);
        return BookingServiceResult.ok(booking);
    }

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> removeService(Long bookingId, Long serviceId) {
        Optional<BookingEntity> current = bookings.findById(bookingId);
        if (current.isEmpty()) {
            return BookingServiceResult.notFound();
        }
        BookingEntity booking = current.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            return BookingServiceResult.notPending();
        }
        Optional<BookingItemEntity> item = findItem(booking, serviceId);
        if (item.isEmpty()) {
            return BookingServiceResult.serviceNotInBooking();
        }

        booking.getItems().remove(item.get());
        recalculateCosts(booking);
        return BookingServiceResult.ok(booking);
    }

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> confirm(Long id) {
        Optional<BookingEntity> current = bookings.findById(id);
        if (current.isEmpty()) {
            return BookingServiceResult.notFound();
        }
        BookingEntity booking = current.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            return BookingServiceResult.invalidTransition();
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return BookingServiceResult.ok(booking);
    }

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> cancel(Long id) {
        Optional<BookingEntity> current = bookings.findById(id);
        if (current.isEmpty()) {
            return BookingServiceResult.notFound();
        }
        BookingEntity booking = current.get();
        if (!ACTIVE_STATUSES.contains(booking.getStatus())) {
            return BookingServiceResult.invalidTransition();
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isAfter(booking.getStartTime().minusHours(2))) {
            return BookingServiceResult.cancellationNotAllowed();
        }

        BigDecimal fee = BigDecimal.ZERO;
        if (now.isAfter(booking.getStartTime().minusHours(24))) {
            fee = booking.getRoomCost()
                    .multiply(new BigDecimal("0.50"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationFee(fee);
        return BookingServiceResult.ok(booking);
    }

    @Override
    @Transactional
    public BookingServiceResult<BookingEntity> complete(Long id) {
        Optional<BookingEntity> current = bookings.findById(id);
        if (current.isEmpty()) {
            return BookingServiceResult.notFound();
        }
        BookingEntity booking = current.get();
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return BookingServiceResult.invalidTransition();
        }
        booking.setStatus(BookingStatus.COMPLETED);
        return BookingServiceResult.ok(booking);
    }

    // ---------- métodos privados de apoyo ----------

    private Optional<BookingItemEntity> findItem(BookingEntity booking, Long serviceId) {
        return booking.getItems().stream()
                .filter(i -> i.getAdditionalService().getId().equals(serviceId))
                .findFirst();
    }

    private BigDecimal calculateRoomCost(MemberEntity member, RoomEntity room,
                                         LocalDateTime start, LocalDateTime end) {
        BigDecimal usedHours = usage.usedHours(member.getId(), YearMonth.from(start));

        MembershipPlanEntity plan = member.getMembershipPlan();
        BigDecimal includedHours = BigDecimal.valueOf(plan.getIncludedHoursPerMonth());
        BigDecimal remainingHours = includedHours.subtract(usedHours).max(BigDecimal.ZERO);
        BigDecimal overageHours = usage.toHours(start, end).subtract(remainingHours).max(BigDecimal.ZERO);

        BigDecimal discountFactor = BigDecimal.ONE.subtract(plan.getOverageDiscount().divide(HUNDRED));
        return overageHours
                .multiply(room.getHourlyRate())
                .multiply(discountFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateServicesCost(List<BookingItemEntity> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (BookingItemEntity item : items) {
            total = total.add(item.getAppliedUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private void recalculateCosts(BookingEntity booking) {
        BigDecimal servicesCost = calculateServicesCost(booking.getItems());
        booking.setServicesCost(servicesCost);
        booking.setTotalCost(booking.getRoomCost().add(servicesCost));
    }
}
