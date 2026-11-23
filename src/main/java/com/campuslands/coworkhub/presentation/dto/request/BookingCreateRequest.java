package com.campuslands.coworkhub.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record BookingCreateRequest(

        @NotNull(message = "El miembro es obligatorio")
        Long memberId,

        @NotNull(message = "La sala es obligatoria")
        Long roomId,

        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        LocalDateTime startTime,

        @NotNull(message = "La fecha y hora de fin son obligatorias")
        LocalDateTime endTime,

        @NotNull(message = "El número de asistentes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 asistente")
        Integer attendees,

        @Valid
        List<BookingItemRequest> services

) {
}
