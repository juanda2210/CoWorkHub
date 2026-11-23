package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AdditionalServiceRequest(
        @Schema(description = "Nombre del servicio adicional. Debe ser único (RN-11)",
                example = "Catering de café")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Schema(description = "Precio por unidad, con hasta 2 decimales", example = "25000.00")
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
        @Digits(integer = 8, fraction = 2,
                message = "El precio unitario admite hasta 8 enteros y 2 decimales")
        BigDecimal unitPrice
) {
}
