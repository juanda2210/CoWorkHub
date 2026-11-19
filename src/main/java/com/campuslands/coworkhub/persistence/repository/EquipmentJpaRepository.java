package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, Long> {

    boolean existsByName(String name);
}
