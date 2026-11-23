package com.campuslands.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record LocationRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 255, message = "La ciudad no puede superar los 255 caracteres")
        String city,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String address,

        @NotNull(message = "La hora de apertura es obligatoria")
        LocalTime openingTime,

        @NotNull(message = "La hora de cierre es obligatoria")
        LocalTime closingTime

) {
}
