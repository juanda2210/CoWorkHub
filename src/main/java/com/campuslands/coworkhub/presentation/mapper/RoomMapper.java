package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.EquipmentEntity;
import com.campuslands.coworkhub.persistence.entity.RoomEntity;
import com.campuslands.coworkhub.presentation.dto.request.RoomCreateRequest;
import com.campuslands.coworkhub.presentation.dto.request.RoomUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.dto.response.RoomResponse;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class RoomMapper {

    private RoomMapper() {
    }

    public static RoomEntity toEntity(RoomCreateRequest request) {
        return new RoomEntity(
                request.name(),
                request.type(),
                request.capacity(),
                request.hourlyRate(),
                request.active(),
                null);
    }

    public static RoomEntity toEntity(RoomUpdateRequest request) {
        return new RoomEntity(
                request.name(),
                request.type(),
                request.capacity(),
                request.hourlyRate(),
                request.active(),
                null);
    }

    public static Set<Long> toEquipmentIds(Set<Long> equipmentIds) {
        return equipmentIds == null ? Set.of() : equipmentIds;
    }

    public static RoomResponse toResponse(RoomEntity entity) {
        return new RoomResponse(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getCapacity(),
                entity.getHourlyRate(),
                entity.isActive(),
                entity.getLocation().getId(),
                entity.getLocation().getName(),
                entity.getEquipment().stream()
                        .sorted(Comparator.comparing(EquipmentEntity::getId))
                        .map(EquipmentMapper::toResponse)
                        .toList());
    }

    public static List<RoomResponse> toResponseList(List<RoomEntity> rooms) {
        return rooms.stream().map(RoomMapper::toResponse).toList();
    }

    public static PageResponse<RoomResponse> toPageResponse(Page<RoomEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(RoomMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
