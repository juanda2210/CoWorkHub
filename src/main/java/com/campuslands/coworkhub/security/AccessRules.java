package com.campuslands.coworkhub.security;

import com.campuslands.coworkhub.business.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("access")
@AllArgsConstructor
public class AccessRules {

    private final BookingService bookings;

    public boolean isOwnMember(Long memberId) {
        Long ownMemberId = currentMemberId();
        return ownMemberId != null && ownMemberId.equals(memberId);
    }

    @Transactional(readOnly = true)
    public boolean isOwnBooking(Long bookingId) {
        Long ownMemberId = currentMemberId();
        if (ownMemberId == null) {
            return false;
        }
        return ownMemberId.equals(bookings.findById(bookingId).getMember().getId());
    }

    private Long currentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user.getMemberId();
        }
        return null;
    }
}
