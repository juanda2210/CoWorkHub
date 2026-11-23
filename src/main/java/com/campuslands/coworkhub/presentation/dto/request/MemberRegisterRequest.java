package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(

        @Schema(description = "Número de documento del miembro. Debe ser único (RN-11)",
                example = "1043210987")
        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 255, message = "El documento no puede superar los 255 caracteres")
        String documentNumber,

        @Schema(description = "Nombre completo del miembro", example = "María López")
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 255, message = "El nombre completo no puede superar los 255 caracteres")
        String fullName,

        @Schema(description = "Correo electrónico del miembro. Debe ser único (RN-11)",
                example = "maria.lopez@coworkhub.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @Schema(description = "Id del plan de membresía que contrata", example = "2")
        @NotNull(message = "El plan de membresía es obligatorio")
        Long membershipPlanId,

        @Schema(description = "Usuario con el que iniciará sesión. Debe ser único (RN-11)",
                example = "maria")
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
        String username,

        @Schema(description = "Contraseña de la cuenta. Se guarda cifrada con BCrypt",
                example = "una-clave-segura")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password

) {
}
