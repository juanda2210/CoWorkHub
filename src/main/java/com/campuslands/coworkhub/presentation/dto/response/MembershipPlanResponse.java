package com.campuslands.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record MembershipPlanResponse(Long id, String name, int includedHoursPerMonth,
                                     BigDecimal overageDiscount, int maxActiveBookings) {
}