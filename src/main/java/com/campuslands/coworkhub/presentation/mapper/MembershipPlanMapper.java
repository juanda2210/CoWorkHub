package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import com.campuslands.coworkhub.presentation.dto.request.MembershipPlanRequest;
import com.campuslands.coworkhub.presentation.dto.response.MembershipPlanResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class MembershipPlanMapper {

    private MembershipPlanMapper() {
    }

    public static MembershipPlanEntity toEntity(MembershipPlanRequest request) {
        return new MembershipPlanEntity(
                request.name(),
                request.includedHoursPerMonth(),
                request.overageDiscount(),
                request.maxActiveBookings());
    }

    public static MembershipPlanResponse toResponse(MembershipPlanEntity entity) {
        return new MembershipPlanResponse(
                entity.getId(),
                entity.getName(),
                entity.getIncludedHoursPerMonth(),
                entity.getOverageDiscount(),
                entity.getMaxActiveBookings());
    }

    public static PageResponse<MembershipPlanResponse> toPageResponse(Page<MembershipPlanEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(MembershipPlanMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}