package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface RoomService {

    RoomEntity create(RoomEntity data, Long locationId, Set<Long> equipmentIds);

    RoomEntity findById(Long id);

    Page<RoomEntity> findAll(Pageable pageable);

    Page<RoomEntity> findByLocation(Long locationId, Pageable pageable);

    RoomEntity update(Long id, RoomEntity data, Set<Long> equipmentIds);

    void delete(Long id);

    List<RoomEntity> findAvailable(Long locationId, LocalDate date,
                                   LocalTime from, LocalTime to,
                                   Integer minCapacity, Set<Long> equipmentIds);
}
