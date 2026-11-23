package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.AdditionalServiceService;
import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import com.campuslands.coworkhub.persistence.repository.AdditionalServiceJpaRepository;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceJpaRepository additionalServices;
    private final BookingJpaRepository bookings;

    @Override
    @Transactional
    public AdditionalServiceEntity create(AdditionalServiceEntity data) {
        if (additionalServices.existsByName(data.getName())) {
            throw new ConflictException("RN-11",
                    "Ya existe un servicio adicional con el nombre " + data.getName());
        }
        return additionalServices.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public AdditionalServiceEntity findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdditionalServiceEntity> findAll(Pageable pageable) {
        return additionalServices.findAll(pageable);
    }

    @Override
    @Transactional
    public AdditionalServiceEntity update(Long id, AdditionalServiceEntity data) {
        AdditionalServiceEntity service = getOrThrow(id);
        if (additionalServices.existsByNameAndIdNot(data.getName(), id)) {
            throw new ConflictException("RN-11",
                    "Ya existe un servicio adicional con el nombre " + data.getName());
        }

        service.setName(data.getName());
        service.setUnitPrice(data.getUnitPrice());
        return service;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AdditionalServiceEntity service = getOrThrow(id);
        if (bookings.existsByItemsAdditionalServiceId(id)) {
            throw new ConflictException("RN-12",
                    "El servicio adicional está en uso en reservas y no se puede eliminar");
        }
        additionalServices.delete(service);
    }

    private AdditionalServiceEntity getOrThrow(Long id) {
        return additionalServices.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "El servicio adicional con id " + id + " no existe"));
    }
}