package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.AdditionalServiceService;
import com.campuslands.coworkhub.business.serviceResult.AdditionalServiceServiceResult;
import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import com.campuslands.coworkhub.persistence.repository.AdditionalServiceJpaRepository;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceJpaRepository additionalServices;
    private final BookingJpaRepository bookings;

    @Override
    @Transactional
    public AdditionalServiceServiceResult<AdditionalServiceEntity> create(AdditionalServiceEntity data) {
        if (additionalServices.existsByName(data.getName())) {
            return AdditionalServiceServiceResult.nameTaken();
        }
        return AdditionalServiceServiceResult.ok(additionalServices.save(data));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdditionalServiceEntity> findById(Long id) {
        return additionalServices.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdditionalServiceEntity> findAll(Pageable pageable) {
        return additionalServices.findAll(pageable);
    }

    @Override
    @Transactional
    public AdditionalServiceServiceResult<AdditionalServiceEntity> update(Long id, AdditionalServiceEntity data) {
        Optional<AdditionalServiceEntity> current = additionalServices.findById(id);
        if (current.isEmpty()) {
            return AdditionalServiceServiceResult.notFound();
        }
        if (additionalServices.existsByNameAndIdNot(data.getName(), id)) {
            return AdditionalServiceServiceResult.nameTaken();
        }

        AdditionalServiceEntity service = current.get();
        service.setName(data.getName());
        service.setUnitPrice(data.getUnitPrice());
        return AdditionalServiceServiceResult.ok(service);
    }

    @Override
    @Transactional
    public AdditionalServiceServiceResult<Void> delete(Long id) {
        Optional<AdditionalServiceEntity> current = additionalServices.findById(id);
        if (current.isEmpty()) {
            return AdditionalServiceServiceResult.notFound();
        }
        if (bookings.existsByItemsAdditionalServiceId(id)) {
            return AdditionalServiceServiceResult.inUse();
        }
        additionalServices.delete(current.get());
        return AdditionalServiceServiceResult.ok(null);
    }
}