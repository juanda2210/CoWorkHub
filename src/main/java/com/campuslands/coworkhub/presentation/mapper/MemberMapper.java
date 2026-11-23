package com.campuslands.coworkhub.presentation.mapper;

import com.campuslands.coworkhub.business.model.MonthlyConsumption;
import com.campuslands.coworkhub.persistence.entity.MemberEntity;
import com.campuslands.coworkhub.presentation.dto.request.MemberRegisterRequest;
import com.campuslands.coworkhub.presentation.dto.request.MemberUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.MemberResponse;
import com.campuslands.coworkhub.presentation.dto.response.MonthlyConsumptionResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class MemberMapper {

    private MemberMapper() {
    }

    public static MemberEntity toEntity(MemberRegisterRequest request) {
        return new MemberEntity(
                request.documentNumber(),
                request.fullName(),
                request.email(),
                null);
    }

    public static MemberEntity toEntity(MemberUpdateRequest request) {
        return new MemberEntity(
                null,
                request.fullName(),
                request.email(),
                null);
    }

    public static MemberResponse toResponse(MemberEntity entity) {
        return new MemberResponse(
                entity.getId(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getMembershipPlan().getId(),
                entity.getMembershipPlan().getName());
    }

    public static PageResponse<MemberResponse> toPageResponse(Page<MemberEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(MemberMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    public static MonthlyConsumptionResponse toConsumptionResponse(MonthlyConsumption consumption) {
        return new MonthlyConsumptionResponse(
                consumption.includedHours(),
                consumption.usedHours(),
                consumption.remainingHours(),
                consumption.overageHours());
    }
}
