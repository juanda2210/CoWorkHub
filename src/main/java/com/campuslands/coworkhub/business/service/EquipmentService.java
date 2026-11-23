package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipmentService {

    EquipmentEntity create(EquipmentEntity data);

    EquipmentEntity findById(Long id);

    Page<EquipmentEntity> findAll(Pageable pageable);

    EquipmentEntity update(Long id, EquipmentEntity data);

    void delete(Long id);
}
