package com.campuslands.coworkhub.presentation.dto.request;

import com.campuslands.coworkhub.persistence.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record RoomCreateRequest(

        @Schema(description = "Id de la sede a la que pertenece la sala", example = "1")
        @NotNull(message = "La sede es obligatoria")
        Long locationId,

        @Schema(description = "Nombre de la sala. Debe ser único dentro de su sede (RN-11)",
                example = "Sala Horizonte")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @Schema(description = "Tipo de sala", example = "MEETING_ROOM")
        @NotNull(message = "El tipo de sala es obligatorio")
        RoomType type,

        @Schema(description = "Capacidad máxima de personas", example = "8")
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacity,

        @Schema(description = "Tarifa por hora, con hasta 2 decimales", example = "45000.00")
        @NotNull(message = "La tarifa por hora es obligatoria")
        @DecimalMin(value = "0.00", message = "La tarifa por hora no puede ser negativa")
        @Digits(integer = 8, fraction = 2,
                message = "La tarifa por hora admite hasta 8 enteros y 2 decimales")
        BigDecimal hourlyRate,

        @Schema(description = "Indica si la sala está activa. Una sala inactiva no se puede reservar",
                example = "true")
        @NotNull(message = "Debe indicar si la sala está activa")
        Boolean active,

        @Schema(description = "Ids del equipamiento de la sala. Si no se envía, la sala queda sin equipamiento",
                example = "[1, 2]")
        Set<Long> equipmentIds

) {
}
