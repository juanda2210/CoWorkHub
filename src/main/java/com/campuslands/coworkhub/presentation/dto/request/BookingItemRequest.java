package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingItemRequest(

        @Schema(description = "Id del servicio adicional", example = "1")
        @NotNull(message = "El servicio adicional es obligatorio")
        Long serviceId,

        @Schema(description = "Cantidad de unidades del servicio", example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity

) {
}
