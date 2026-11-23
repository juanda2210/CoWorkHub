package com.campuslands.coworkhub.presentation.dto;

import java.time.Instant;

public record ErrorResponse(String error, String codigo, Instant timestamp) {
}
