package com.campuslands.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record MonthlyConsumptionResponse(BigDecimal includedHours, BigDecimal usedHours,
                                         BigDecimal remainingHours, BigDecimal overageHours) {
}
