package com.campuslands.coworkhub.presentation.dto.request;

import com.campuslands.coworkhub.persistence.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record RoomCreateRequest(

        @NotNull(message = "La sede es obligatoria")
        Long locationId,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @NotNull(message = "El tipo de sala es obligatorio")
        RoomType type,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacity,

        @NotNull(message = "La tarifa por hora es obligatoria")
        @DecimalMin(value = "0.00", message = "La tarifa por hora no puede ser negativa")
        @Digits(integer = 8, fraction = 2,
                message = "La tarifa por hora admite hasta 8 enteros y 2 decimales")
        BigDecimal hourlyRate,

        @NotNull(message = "Debe indicar si la sala está activa")
        Boolean active,

        Set<Long> equipmentIds

) {
}
