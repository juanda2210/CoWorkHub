package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.serviceResult.LocationServiceResult;
import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LocationService {

    LocationServiceResult<LocationEntity> create(LocationEntity data);

    Optional<LocationEntity> findById(Long id);

    Page<LocationEntity> findAll(Pageable pageable);

    LocationServiceResult<LocationEntity> update(Long id, LocationEntity data);

    LocationServiceResult<Void> delete(Long id);
}