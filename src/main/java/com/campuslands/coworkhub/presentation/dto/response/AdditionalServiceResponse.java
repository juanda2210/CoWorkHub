package com.campuslands.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record AdditionalServiceResponse(Long id, String name, BigDecimal unitPrice) {
}