package com.campuslands.coworkhub.security;

import com.campuslands.coworkhub.presentation.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@AllArgsConstructor
public class SecurityErrorWriter {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public void write(HttpServletResponse response, HttpStatus status,
                      String code, String message) throws IOException {
        ErrorResponse body = new ErrorResponse(
                message, code, Instant.now(clock).truncatedTo(ChronoUnit.SECONDS));
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
