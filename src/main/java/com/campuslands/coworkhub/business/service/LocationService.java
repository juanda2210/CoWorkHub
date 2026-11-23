package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationService {

    LocationEntity create(LocationEntity data);

    LocationEntity findById(Long id);

    Page<LocationEntity> findAll(Pageable pageable);

    LocationEntity update(Long id, LocationEntity data);

    void delete(Long id);
}
