package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import com.campuslands.coworkhub.presentation.dto.request.AdditionalServiceRequest;
import com.campuslands.coworkhub.presentation.dto.response.AdditionalServiceResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class AdditionalServiceMapper {

    private AdditionalServiceMapper() {
    }

    public static AdditionalServiceEntity toEntity(AdditionalServiceRequest request) {
        return new AdditionalServiceEntity(request.name(), request.unitPrice());
    }

    public static AdditionalServiceResponse toResponse(AdditionalServiceEntity entity) {
        return new AdditionalServiceResponse(entity.getId(), entity.getName(), entity.getUnitPrice());
    }

    public static PageResponse<AdditionalServiceResponse> toPageResponse(Page<AdditionalServiceEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(AdditionalServiceMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}