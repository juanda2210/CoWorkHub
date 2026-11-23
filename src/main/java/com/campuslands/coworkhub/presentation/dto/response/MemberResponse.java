package com.campuslands.coworkhub.presentation.dto.response;

import com.campuslands.coworkhub.persistence.enums.MemberStatus;

public record MemberResponse(Long id, String documentNumber, String fullName, String email,
                             MemberStatus status, Long membershipPlanId, String membershipPlanName) {
}
