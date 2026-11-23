package com.campuslands.coworkhub.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Cuerpo único de todas las respuestas de error de la API")
public record ErrorResponse(

        @Schema(description = "Mensaje del error, pensado para que lo lea una persona",
                example = "La sala ya está reservada en ese horario")
        String error,

        @Schema(description = "Regla de negocio violada (RN-01, RN-11...) o tipo de error "
                + "(NOT_FOUND, VALIDATION_ERROR, UNAUTHORIZED, FORBIDDEN)", example = "RN-01")
        String codigo,

        @Schema(description = "Momento en que ocurrió el error, en UTC", example = "2030-06-03T13:00:00Z")
        Instant timestamp

) {
}
