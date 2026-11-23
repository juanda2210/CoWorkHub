package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.EquipmentService;
import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import com.campuslands.coworkhub.persistence.repository.EquipmentJpaRepository;
import com.campuslands.coworkhub.persistence.repository.RoomJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentJpaRepository equipment;
    private final RoomJpaRepository rooms;

    @Override
    @Transactional
    public EquipmentEntity create(EquipmentEntity data) {
        if (equipment.existsByName(data.getName())) {
            throw new ConflictException("RN-11",
                    "Ya existe un equipamiento con el nombre " + data.getName());
        }
        return equipment.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentEntity findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentEntity> findAll(Pageable pageable) {
        return equipment.findAll(pageable);
    }

    @Override
    @Transactional
    public EquipmentEntity update(Long id, EquipmentEntity data) {
        EquipmentEntity item = getOrThrow(id);
        if (equipment.existsByNameAndIdNot(data.getName(), id)) {
            throw new ConflictException("RN-11",
                    "Ya existe un equipamiento con el nombre " + data.getName());
        }

        item.setName(data.getName());
        item.setQuantity(data.getQuantity());
        return item;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EquipmentEntity item = getOrThrow(id);
        if (rooms.existsByEquipmentId(id)) {
            throw new ConflictException("RN-12",
                    "El equipamiento está en uso en salas y no se puede eliminar");
        }
        equipment.delete(item);
    }

    private EquipmentEntity getOrThrow(Long id) {
        return equipment.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "El equipamiento con id " + id + " no existe"));
    }
}
