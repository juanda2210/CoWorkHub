package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MembershipPlanRequest(

        @Schema(description = "Nombre del plan. Debe ser único (RN-11)", example = "Corporativo")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @Schema(description = "Horas que el plan incluye cada mes, sin costo", example = "40")
        @NotNull(message = "Las horas incluidas por mes son obligatorias")
        @Min(value = 0, message = "Las horas incluidas no pueden ser negativas")
        Integer includedHoursPerMonth,

        @Schema(description = "Porcentaje de descuento (de 0 a 100) sobre las horas que exceden lo incluido",
                example = "25.00")
        @NotNull(message = "El descuento por excedente es obligatorio")
        @DecimalMin(value = "0.00", message = "El descuento no puede ser menor que 0")
        @DecimalMax(value = "100.00", message = "El descuento no puede ser mayor que 100")
        @Digits(integer = 3, fraction = 2,
                message = "El descuento admite hasta 3 enteros y 2 decimales")
        BigDecimal overageDiscount,

        @Schema(description = "Máximo de reservas activas futuras que puede tener un miembro del plan",
                example = "8")
        @NotNull(message = "El máximo de reservas activas es obligatorio")
        @Min(value = 1, message = "El máximo de reservas activas debe ser al menos 1")
        Integer maxActiveBookings

) {
}
