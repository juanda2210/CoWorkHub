package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import com.campuslands.coworkhub.presentation.dto.request.EquipmentRequest;
import com.campuslands.coworkhub.presentation.dto.response.EquipmentResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class EquipmentMapper {

    private EquipmentMapper() {
    }

    public static EquipmentEntity toEntity(EquipmentRequest request) {
        return new EquipmentEntity(request.name(), request.quantity());
    }

    public static EquipmentResponse toResponse(EquipmentEntity entity) {
        return new EquipmentResponse(entity.getId(), entity.getName(), entity.getQuantity());
    }

    public static PageResponse<EquipmentResponse> toPageResponse(Page<EquipmentEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(EquipmentMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}