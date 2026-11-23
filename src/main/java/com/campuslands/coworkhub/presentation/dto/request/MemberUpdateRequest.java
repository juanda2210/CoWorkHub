package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(

        @Schema(description = "Nombre completo del miembro", example = "María López Ruiz")
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 255, message = "El nombre completo no puede superar los 255 caracteres")
        String fullName,

        @Schema(description = "Correo electrónico. No puede ser el de otro miembro (RN-11)",
                example = "maria.lopez@coworkhub.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @Schema(description = "Id del nuevo plan de membresía", example = "3")
        @NotNull(message = "El plan de membresía es obligatorio")
        Long membershipPlanId

) {
}
