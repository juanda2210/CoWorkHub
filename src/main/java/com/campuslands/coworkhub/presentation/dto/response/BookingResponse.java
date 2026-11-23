package com.campuslands.coworkhub.presentation.dto.response;

import com.campuslands.coworkhub.persistence.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(Long id, LocalDateTime startTime, LocalDateTime endTime,
                              int attendees, BookingStatus status,
                              BigDecimal roomCost, BigDecimal servicesCost, BigDecimal totalCost,
                              BigDecimal cancellationFee,
                              Long memberId, String memberName,
                              Long roomId, String roomName,
                              List<BookingItemResponse> items) {
}
