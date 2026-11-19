package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.RoomEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {

    List<RoomEntity> findByLocationId(Long locationId);

    boolean existsByLocationIdAndName(Long locationId, String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomEntity> findWithLockById(Long id);

    boolean existsByLocationId(Long locationId);

    boolean existsByEquipmentId(Long equipmentId);

}
