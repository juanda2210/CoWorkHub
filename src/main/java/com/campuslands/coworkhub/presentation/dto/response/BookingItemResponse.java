package com.campuslands.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record BookingItemResponse(Long serviceId, String serviceName,
                                  int quantity, BigDecimal appliedUnitPrice) {
}
