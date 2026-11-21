package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.LocationService;
import com.campuslands.coworkhub.business.serviceResult.LocationServiceResult;
import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import com.campuslands.coworkhub.persistence.repository.LocationJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationJpaRepository locations;
    private final RoomJpaRepository rooms;

    @Override
    @Transactional
    public LocationServiceResult<LocationEntity> create(LocationEntity data) {
        if (!data.getOpeningTime().isBefore(data.getClosingTime())) {
            return LocationServiceResult.invalidSchedule();
        }
        return LocationServiceResult.ok(locations.save(data));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocationEntity> findById(Long id) {
        return locations.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationEntity> findAll(Pageable pageable) {
        return locations.findAll(pageable);
    }

    @Override
    @Transactional
    public LocationServiceResult<LocationEntity> update(Long id, LocationEntity data) {
        Optional<LocationEntity> current = locations.findById(id);
        if (current.isEmpty()) {
            return LocationServiceResult.notFound();
        }
        if (!data.getOpeningTime().isBefore(data.getClosingTime())) {
            return LocationServiceResult.invalidSchedule();
        }

        LocationEntity location = current.get();
        location.setName(data.getName());
        location.setCity(data.getCity());
        location.setAddress(data.getAddress());
        location.setOpeningTime(data.getOpeningTime());
        location.setClosingTime(data.getClosingTime());
        return LocationServiceResult.ok(location);
    }

    @Override
    @Transactional
    public LocationServiceResult<Void> delete(Long id) {
        Optional<LocationEntity> current = locations.findById(id);
        if (current.isEmpty()) {
            return LocationServiceResult.notFound();
        }
        if (rooms.existsByLocationId(id)) {
            return LocationServiceResult.inUse();
        }
        locations.delete(current.get());
        return LocationServiceResult.ok(null);
    }
}