package com.campuslands.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MembershipPlanRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
        String name,

        @NotNull(message = "Las horas incluidas por mes son obligatorias")
        @Min(value = 0, message = "Las horas incluidas no pueden ser negativas")
        Integer includedHoursPerMonth,

        @NotNull(message = "El descuento por excedente es obligatorio")
        @DecimalMin(value = "0.00", message = "El descuento no puede ser menor que 0")
        @DecimalMax(value = "100.00", message = "El descuento no puede ser mayor que 100")
        @Digits(integer = 3, fraction = 2,
                message = "El descuento admite hasta 3 enteros y 2 decimales")
        BigDecimal overageDiscount,

        @NotNull(message = "El máximo de reservas activas es obligatorio")
        @Min(value = 1, message = "El máximo de reservas activas debe ser al menos 1")
        Integer maxActiveBookings

) {
}