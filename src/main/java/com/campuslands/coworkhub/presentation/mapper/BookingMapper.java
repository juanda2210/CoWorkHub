package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.entity.BookingItemEntity;
import com.campuslands.coworkhub.presentation.dto.request.BookingItemRequest;
import com.campuslands.coworkhub.presentation.dto.response.BookingItemResponse;
import com.campuslands.coworkhub.presentation.dto.response.BookingResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static Map<Long, Integer> toServicesMap(List<BookingItemRequest> items) {
        if (items == null) {
            return Map.of();
        }
        return items.stream().collect(Collectors.toMap(
                BookingItemRequest::serviceId,
                BookingItemRequest::quantity,
                Integer::sum));
    }

    public static BookingResponse toResponse(BookingEntity entity) {
        return new BookingResponse(
                entity.getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getAttendees(),
                entity.getStatus(),
                entity.getRoomCost(),
                entity.getServicesCost(),
                entity.getTotalCost(),
                entity.getCancellationFee(),
                entity.getMember().getId(),
                entity.getMember().getFullName(),
                entity.getRoom().getId(),
                entity.getRoom().getName(),
                entity.getItems().stream()
                        .sorted(Comparator.comparing(BookingItemEntity::getId))
                        .map(BookingMapper::toItemResponse)
                        .toList());
    }

    public static BookingItemResponse toItemResponse(BookingItemEntity item) {
        return new BookingItemResponse(
                item.getAdditionalService().getId(),
                item.getAdditionalService().getName(),
                item.getQuantity(),
                item.getAppliedUnitPrice());
    }

    public static PageResponse<BookingResponse> toPageResponse(Page<BookingEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(BookingMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
