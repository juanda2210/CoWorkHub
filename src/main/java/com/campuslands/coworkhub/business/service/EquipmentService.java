package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.serviceResult.EquipmentServiceResult;
import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EquipmentService {

    EquipmentServiceResult<EquipmentEntity> create(EquipmentEntity data);

    Optional<EquipmentEntity> findById(Long id);

    Page<EquipmentEntity> findAll(Pageable pageable);

    EquipmentServiceResult<EquipmentEntity> update(Long id, EquipmentEntity data);

    EquipmentServiceResult<Void> delete(Long id);
}