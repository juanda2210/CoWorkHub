package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.EquipmentService;
import com.campuslands.coworkhub.business.serviceResult.EquipmentServiceResult;
import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import com.campuslands.coworkhub.persistence.repository.EquipmentJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentJpaRepository equipment;
    private final RoomJpaRepository rooms;

    @Override
    @Transactional
    public EquipmentServiceResult<EquipmentEntity> create(EquipmentEntity data) {
        if (equipment.existsByName(data.getName())) {
            return EquipmentServiceResult.nameTaken();
        }
        return EquipmentServiceResult.ok(equipment.save(data));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipmentEntity> findById(Long id) {
        return equipment.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentEntity> findAll(Pageable pageable) {
        return equipment.findAll(pageable);
    }

    @Override
    @Transactional
    public EquipmentServiceResult<EquipmentEntity> update(Long id, EquipmentEntity data) {
        Optional<EquipmentEntity> current = equipment.findById(id);
        if (current.isEmpty()) {
            return EquipmentServiceResult.notFound();
        }
        if (equipment.existsByNameAndIdNot(data.getName(), id)) {
            return EquipmentServiceResult.nameTaken();
        }

        EquipmentEntity item = current.get();
        item.setName(data.getName());
        item.setQuantity(data.getQuantity());
        return EquipmentServiceResult.ok(item);
    }

    @Override
    @Transactional
    public EquipmentServiceResult<Void> delete(Long id) {
        Optional<EquipmentEntity> current = equipment.findById(id);
        if (current.isEmpty()) {
            return EquipmentServiceResult.notFound();
        }
        if (rooms.existsByEquipmentId(id)) {
            return EquipmentServiceResult.inUse();
        }
        equipment.delete(current.get());
        return EquipmentServiceResult.ok(null);
    }
}