package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.serviceResult.AdditionalServiceServiceResult;
import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdditionalServiceService {

    AdditionalServiceServiceResult<AdditionalServiceEntity> create(AdditionalServiceEntity data);

    Optional<AdditionalServiceEntity> findById(Long id);

    Page<AdditionalServiceEntity> findAll(Pageable pageable);

    AdditionalServiceServiceResult<AdditionalServiceEntity> update(Long id, AdditionalServiceEntity data);

    AdditionalServiceServiceResult<Void> delete(Long id);
}