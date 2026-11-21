package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.RoomService;
import com.campuslands.coworkhub.business.serviceResult.RoomServiceResult;
import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import com.campuslands.coworkhub.persistence.entity.RoomEntity;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import com.campuslands.coworkhub.persistence.repository.EquipmentJpaRepository;
import com.campuslands.coworkhub.persistence.repository.LocationJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final RoomJpaRepository rooms;
    private final LocationJpaRepository locations;
    private final EquipmentJpaRepository equipment;
    private final BookingJpaRepository bookings;

    @Override
    @Transactional
    public RoomServiceResult<RoomEntity> create(RoomEntity data, Long locationId, Set<Long> equipmentIds) {
        Optional<LocationEntity> location = locations.findById(locationId);
        if (location.isEmpty()) {
            return RoomServiceResult.locationNotFound();
        }
        if (rooms.existsByLocationIdAndName(locationId, data.getName())) {
            return RoomServiceResult.nameTaken();
        }
        Set<EquipmentEntity> equipmentSet = new HashSet<>(equipment.findAllById(equipmentIds));
        if (equipmentSet.size() != equipmentIds.size()) {
            return RoomServiceResult.equipmentNotFound();
        }

        data.setLocation(location.get());
        data.setEquipment(equipmentSet);
        return RoomServiceResult.ok(rooms.save(data));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomEntity> findById(Long id) {
        return rooms.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomEntity> findAll(Pageable pageable) {
        return rooms.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> findByLocation(Long locationId) {
        return rooms.findByLocationId(locationId);
    }

    @Override
    @Transactional
    public RoomServiceResult<RoomEntity> update(Long id, RoomEntity data, Set<Long> equipmentIds) {
        Optional<RoomEntity> current = rooms.findById(id);
        if (current.isEmpty()) {
            return RoomServiceResult.notFound();
        }
        RoomEntity room = current.get();

        if (rooms.existsByLocationIdAndNameAndIdNot(room.getLocation().getId(), data.getName(), id)) {
            return RoomServiceResult.nameTaken();
        }
        Set<EquipmentEntity> equipmentSet = new HashSet<>(equipment.findAllById(equipmentIds));
        if (equipmentSet.size() != equipmentIds.size()) {
            return RoomServiceResult.equipmentNotFound();
        }

        room.setName(data.getName());
        room.setType(data.getType());
        room.setCapacity(data.getCapacity());
        room.setHourlyRate(data.getHourlyRate());
        room.setActive(data.isActive());
        room.getEquipment().clear();
        room.getEquipment().addAll(equipmentSet);
        return RoomServiceResult.ok(room);
    }

    @Override
    @Transactional
    public RoomServiceResult<Void> delete(Long id) {
        Optional<RoomEntity> current = rooms.findById(id);
        if (current.isEmpty()) {
            return RoomServiceResult.notFound();
        }
        if (bookings.existsByRoomId(id)) {
            return RoomServiceResult.inUse();
        }
        rooms.delete(current.get());
        return RoomServiceResult.ok(null);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomServiceResult<List<RoomEntity>> findAvailable(Long locationId, LocalDate date,
                                                             LocalTime from, LocalTime to,
                                                             Integer minCapacity, Set<Long> equipmentIds) {
        if (!from.isBefore(to)) {
            return RoomServiceResult.invalidTimeRange();
        }
        if (!locations.existsById(locationId)) {
            return RoomServiceResult.locationNotFound();
        }

        LocalDateTime start = date.atTime(from);
        LocalDateTime end = date.atTime(to);

        Set<Long> busyRoomIds = new HashSet<>();
        List<BookingEntity> crossed = bookings
                .findByRoomLocationIdAndStartTimeLessThanAndEndTimeGreaterThan(locationId, end, start);
        for (BookingEntity booking : crossed) {
            if (ACTIVE_STATUSES.contains(booking.getStatus())) {
                busyRoomIds.add(booking.getRoom().getId());
            }
        }

        List<RoomEntity> available = new ArrayList<>();
        for (RoomEntity room : rooms.findByLocationId(locationId)) {
            if (!room.isActive()) {
                continue;
            }
            if (busyRoomIds.contains(room.getId())) {
                continue;
            }
            if (minCapacity != null && room.getCapacity() < minCapacity) {
                continue;
            }
            if (equipmentIds != null && !equipmentIds.isEmpty()) {
                Set<Long> roomEquipmentIds = new HashSet<>();
                for (EquipmentEntity item : room.getEquipment()) {
                    roomEquipmentIds.add(item.getId());
                }
                if (!roomEquipmentIds.containsAll(equipmentIds)) {
                    continue;
                }
            }
            available.add(room);
        }
        return RoomServiceResult.ok(available);
    }
}