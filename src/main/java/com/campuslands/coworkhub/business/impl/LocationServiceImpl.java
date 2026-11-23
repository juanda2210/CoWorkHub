package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.InvalidDataException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.LocationService;
import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import com.campuslands.coworkhub.persistence.repository.LocationJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationJpaRepository locations;
    private final RoomJpaRepository rooms;

    @Override
    @Transactional
    public LocationEntity create(LocationEntity data) {
        validateSchedule(data);
        return locations.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationEntity findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationEntity> findAll(Pageable pageable) {
        return locations.findAll(pageable);
    }

    @Override
    @Transactional
    public LocationEntity update(Long id, LocationEntity data) {
        LocationEntity location = getOrThrow(id);
        validateSchedule(data);

        location.setName(data.getName());
        location.setCity(data.getCity());
        location.setAddress(data.getAddress());
        location.setOpeningTime(data.getOpeningTime());
        location.setClosingTime(data.getClosingTime());
        return location;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LocationEntity location = getOrThrow(id);
        if (rooms.existsByLocationId(id)) {
            throw new ConflictException("RN-12",
                    "La sede tiene salas y no se puede eliminar");
        }
        locations.delete(location);
    }

    private LocationEntity getOrThrow(Long id) {
        return locations.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "La sede con id " + id + " no existe"));
    }

    private void validateSchedule(LocationEntity data) {
        if (!data.getOpeningTime().isBefore(data.getClosingTime())) {
            throw new InvalidDataException(
                    "La hora de apertura debe ser anterior a la hora de cierre");
        }
    }
}
