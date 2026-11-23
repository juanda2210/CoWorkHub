package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record LocationRequest(

        @Schema(description = "Nombre de la sede", example = "Sede Norte")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @Schema(description = "Ciudad de la sede", example = "Medellín")
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 255, message = "La ciudad no puede superar los 255 caracteres")
        String city,

        @Schema(description = "Dirección de la sede", example = "Calle 10 # 40-20")
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String address,

        @Schema(description = "Hora de apertura. Debe ser anterior a la hora de cierre",
                example = "08:00:00")
        @NotNull(message = "La hora de apertura es obligatoria")
        LocalTime openingTime,

        @Schema(description = "Hora de cierre", example = "20:00:00")
        @NotNull(message = "La hora de cierre es obligatoria")
        LocalTime closingTime

) {
}
