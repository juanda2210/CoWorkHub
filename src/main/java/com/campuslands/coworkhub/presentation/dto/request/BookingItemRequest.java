package com.campuslands.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingItemRequest(

        @NotNull(message = "El servicio adicional es obligatorio")
        Long serviceId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity

) {
}
