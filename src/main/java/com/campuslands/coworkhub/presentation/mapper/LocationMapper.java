package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import com.campuslands.coworkhub.presentation.dto.request.LocationRequest;
import com.campuslands.coworkhub.presentation.dto.response.LocationResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class LocationMapper {

    private LocationMapper() {
    }

    public static LocationEntity toEntity(LocationRequest request) {
        return new LocationEntity(
                request.name(),
                request.city(),
                request.address(),
                request.openingTime(),
                request.closingTime());
    }

    public static LocationResponse toResponse(LocationEntity entity) {
        return new LocationResponse(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getAddress(),
                entity.getOpeningTime(),
                entity.getClosingTime());
    }

    public static PageResponse<LocationResponse> toPageResponse(Page<LocationEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(LocationMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
