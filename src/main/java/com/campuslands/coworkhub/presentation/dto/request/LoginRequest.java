package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Schema(description = "Nombre de usuario", example = "ana")
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña del usuario", example = "su-contraseña")
        @NotBlank(message = "La contraseña es obligatoria")
        String password

) {
}
