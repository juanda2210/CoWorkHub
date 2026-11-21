package com.campuslands.coworkhub.business.model;

import java.math.BigDecimal;

public record MonthlyConsumption(BigDecimal includedHours,
                                 BigDecimal usedHours,
                                 BigDecimal remainingHours,
                                 BigDecimal overageHours) {
}
