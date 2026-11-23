package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.component.MonthlyUsageCalculator;
import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.BookingService;
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
    public BookingEntity create(Long memberId, Long roomId,
                                LocalDateTime startTime, LocalDateTime endTime,
                                int attendees, Map<Long, Integer> services) {
        // El candado de la sala debe ser lo PRIMERO que se hace en la transacción. MySQL toma la
        // "foto" de lectura en la primera consulta normal; si se toma antes de esperar el candado,
        // la validación de cruces (RN-01) no vería las reservas de la otra solicitud simultánea.
        RoomEntity room = rooms.findWithLockById(roomId)
                .orElseThrow(() -> new NotFoundException(
                        "La sala con id " + roomId + " no existe"));

        MemberEntity member = members.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        "El miembro con id " + memberId + " no existe"));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new ConflictException("RN-05",
                    "El miembro no está activo y no puede reservar");
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        boolean sameDay = startTime.toLocalDate().equals(endTime.toLocalDate());
        if (!sameDay || minutes < 30 || minutes > 480 || minutes % 30 != 0) {
            throw new ConflictException("RN-02",
                    "La reserva debe durar entre 30 minutos y 8 horas, en múltiplos de 30 minutos, "
                            + "y empezar y terminar el mismo día");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (startTime.isBefore(now.plusHours(1))) {
            throw new ConflictException("RN-03",
                    "La reserva debe ser futura y hacerse con al menos 1 hora de anticipación");
        }

        if (!room.isActive()) {
            throw new ConflictException("RN-04", "La sala no está activa");
        }

        if (attendees > room.getCapacity()) {
            throw new ConflictException("RN-04",
                    "Los asistentes superan la capacidad de la sala (" + room.getCapacity() + ")");
        }
        LocationEntity location = room.getLocation();

        if (startTime.toLocalTime().isBefore(location.getOpeningTime())
                || endTime.toLocalTime().isAfter(location.getClosingTime())) {
            throw new ConflictException("RN-03",
                    "La reserva queda fuera del horario de la sede");
        }

        long activeBookings = bookings.countByMemberIdAndStatusInAndStartTimeAfter(
                memberId, ACTIVE_STATUSES, now);
        if (activeBookings >= member.getMembershipPlan().getMaxActiveBookings()) {
            throw new ConflictException("RN-06",
                    "El miembro alcanzó el máximo de reservas activas de su plan");
        }

        boolean overlaps = bookings
                .findByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(roomId, endTime, startTime)
                .stream()
                .anyMatch(b -> ACTIVE_STATUSES.contains(b.getStatus()));
        if (overlaps) {
            throw new ConflictException("RN-01",
                    "La sala ya está reservada en ese horario");
        }

        List<BookingItemEntity> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : services.entrySet()) {
            AdditionalServiceEntity service = additionalServices.findById(entry.getKey())
                    .orElseThrow(() -> new NotFoundException(
                            "El servicio adicional con id " + entry.getKey() + " no existe"));
            items.add(new BookingItemEntity(
                    entry.getValue(), service.getUnitPrice(), null, service));
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

        return bookings.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingEntity findById(Long id) {
        return getOrThrow(id);
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
    public BookingEntity addService(Long bookingId, Long serviceId, int quantity) {
        BookingEntity booking = getOrThrow(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("RN-10",
                    "Los servicios solo se pueden cambiar mientras la reserva está PENDING");
        }
        AdditionalServiceEntity service = additionalServices.findById(serviceId)
                .orElseThrow(() -> new NotFoundException(
                        "El servicio adicional con id " + serviceId + " no existe"));

        Optional<BookingItemEntity> existing = findItem(booking, serviceId);
        if (existing.isPresent()) {
            BookingItemEntity item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            booking.getItems().add(new BookingItemEntity(
                    quantity, service.getUnitPrice(), booking, service));
        }

        recalculateCosts(booking);
        return booking;
    }

    @Override
    @Transactional
    public BookingEntity removeService(Long bookingId, Long serviceId) {
        BookingEntity booking = getOrThrow(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("RN-10",
                    "Los servicios solo se pueden cambiar mientras la reserva está PENDING");
        }
        BookingItemEntity item = findItem(booking, serviceId)
                .orElseThrow(() -> new NotFoundException(
                        "La reserva no tiene el servicio adicional con id " + serviceId));

        booking.getItems().remove(item);
        recalculateCosts(booking);
        return booking;
    }

    @Override
    @Transactional
    public BookingEntity confirm(Long id) {
        BookingEntity booking = getOrThrow(id);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("RN-09",
                    "No se puede confirmar una reserva en estado " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    @Override
    @Transactional
    public BookingEntity cancel(Long id) {
        BookingEntity booking = getOrThrow(id);
        if (!ACTIVE_STATUSES.contains(booking.getStatus())) {
            throw new ConflictException("RN-09",
                    "No se puede cancelar una reserva en estado " + booking.getStatus());
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (now.isAfter(booking.getStartTime().minusHours(2))) {
            throw new ConflictException("RN-08",
                    "No se puede cancelar con menos de 2 horas de anticipación o si la reserva ya empezó");
        }

        BigDecimal fee = BigDecimal.ZERO;
        if (now.isAfter(booking.getStartTime().minusHours(24))) {
            fee = booking.getRoomCost()
                    .multiply(new BigDecimal("0.50"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationFee(fee);
        return booking;
    }

    @Override
    @Transactional
    public BookingEntity complete(Long id) {
        BookingEntity booking = getOrThrow(id);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("RN-09",
                    "No se puede completar una reserva en estado " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.COMPLETED);
        return booking;
    }

    // ---------- métodos privados de apoyo ----------

    private BookingEntity getOrThrow(Long id) {
        return bookings.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "La reserva con id " + id + " no existe"));
    }

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
