package com.campuslands.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record BookingCreateRequest(

        @Schema(description = "Id del miembro a cuyo nombre se hace la reserva. "
                + "Un MEMBER solo puede usar el suyo", example = "1")
        @NotNull(message = "El miembro es obligatorio")
        Long memberId,

        @Schema(description = "Id de la sala", example = "1")
        @NotNull(message = "La sala es obligatoria")
        Long roomId,

        @Schema(description = "Fecha y hora de inicio. Debe ser futura, con al menos 1 hora de "
                + "anticipación y dentro del horario de la sede", example = "2030-06-03T10:00:00")
        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        LocalDateTime startTime,

        @Schema(description = "Fecha y hora de fin. Mismo día del inicio; la reserva dura entre "
                + "30 minutos y 8 horas, en múltiplos de 30 minutos", example = "2030-06-03T11:00:00")
        @NotNull(message = "La fecha y hora de fin son obligatorias")
        LocalDateTime endTime,

        @Schema(description = "Número de asistentes. No puede superar la capacidad de la sala",
                example = "4")
        @NotNull(message = "El número de asistentes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 asistente")
        Integer attendees,

        @Schema(description = "Servicios adicionales de la reserva (opcional)")
        @Valid
        List<BookingItemRequest> services

) {
}
