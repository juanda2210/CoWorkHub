package com.campuslands.coworkhub.presentation.dto.response;

public record LoginResponse(String token, String tokenType, long expiresIn) {
}
