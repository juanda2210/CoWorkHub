package com.campuslands.coworkhub.presentation.dto.response;

import com.campuslands.coworkhub.persistence.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record RoomResponse(Long id, String name, RoomType type, int capacity,
                           BigDecimal hourlyRate, boolean active,
                           Long locationId, String locationName,
                           List<EquipmentResponse> equipment) {
}
