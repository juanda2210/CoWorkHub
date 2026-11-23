package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdditionalServiceService {

    AdditionalServiceEntity create(AdditionalServiceEntity data);

    AdditionalServiceEntity findById(Long id);

    Page<AdditionalServiceEntity> findAll(Pageable pageable);

    AdditionalServiceEntity update(Long id, AdditionalServiceEntity data);

    void delete(Long id);
}