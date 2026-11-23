package com.campuslands.coworkhub.presentation.dto.response;

import java.time.LocalTime;

public record LocationResponse(Long id, String name, String city, String address,
                               LocalTime openingTime, LocalTime closingTime) {
}
