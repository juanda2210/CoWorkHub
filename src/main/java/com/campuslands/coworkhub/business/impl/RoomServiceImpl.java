package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.InvalidDataException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.RoomService;
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
    public RoomEntity create(RoomEntity data, Long locationId, Set<Long> equipmentIds) {
        LocationEntity location = locations.findById(locationId)
                .orElseThrow(() -> new NotFoundException(
                        "La sede con id " + locationId + " no existe"));
        if (rooms.existsByLocationIdAndName(locationId, data.getName())) {
            throw new ConflictException("RN-11",
                    "Ya existe una sala con el nombre " + data.getName() + " en esa sede");
        }
        Set<EquipmentEntity> equipmentSet = loadEquipment(equipmentIds);

        data.setLocation(location);
        data.setEquipment(equipmentSet);
        return rooms.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomEntity findById(Long id) {
        return getOrThrow(id);
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
    public RoomEntity update(Long id, RoomEntity data, Set<Long> equipmentIds) {
        RoomEntity room = getOrThrow(id);

        if (rooms.existsByLocationIdAndNameAndIdNot(room.getLocation().getId(), data.getName(), id)) {
            throw new ConflictException("RN-11",
                    "Ya existe una sala con el nombre " + data.getName() + " en esa sede");
        }
        Set<EquipmentEntity> equipmentSet = loadEquipment(equipmentIds);

        room.setName(data.getName());
        room.setType(data.getType());
        room.setCapacity(data.getCapacity());
        room.setHourlyRate(data.getHourlyRate());
        room.setActive(data.isActive());
        room.getEquipment().clear();
        room.getEquipment().addAll(equipmentSet);
        return room;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoomEntity room = getOrThrow(id);
        if (bookings.existsByRoomId(id)) {
            throw new ConflictException("RN-12",
                    "La sala tiene reservas y no se puede eliminar");
        }
        rooms.delete(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> findAvailable(Long locationId, LocalDate date,
                                          LocalTime from, LocalTime to,
                                          Integer minCapacity, Set<Long> equipmentIds) {
        if (!from.isBefore(to)) {
            throw new InvalidDataException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }
        if (!locations.existsById(locationId)) {
            throw new NotFoundException("La sede con id " + locationId + " no existe");
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
        return available;
    }

    private RoomEntity getOrThrow(Long id) {
        return rooms.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "La sala con id " + id + " no existe"));
    }

    private Set<EquipmentEntity> loadEquipment(Set<Long> equipmentIds) {
        Set<EquipmentEntity> equipmentSet = new HashSet<>(equipment.findAllById(equipmentIds));
        if (equipmentSet.size() != equipmentIds.size()) {
            throw new NotFoundException("Alguno de los equipamientos indicados no existe");
        }
        return equipmentSet;
    }
}
