package com.campuslands.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 255, message = "El nombre completo no puede superar los 255 caracteres")
        String fullName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @NotNull(message = "El plan de membresía es obligatorio")
        Long membershipPlanId

) {
}
