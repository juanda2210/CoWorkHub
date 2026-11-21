package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.MembershipPlanServiceStatus;

public record MembershipPlanServiceResult<T>(MembershipPlanServiceStatus status, T value) {

    public static <T> MembershipPlanServiceResult<T> ok(T value) {
        return new MembershipPlanServiceResult<>(MembershipPlanServiceStatus.OK, value);
    }

    public static <T> MembershipPlanServiceResult<T> notFound() {
        return new MembershipPlanServiceResult<>(MembershipPlanServiceStatus.NOT_FOUND, null);
    }

    public static <T> MembershipPlanServiceResult<T> nameTaken() {
        return new MembershipPlanServiceResult<>(MembershipPlanServiceStatus.NAME_TAKEN, null);
    }

    public static <T> MembershipPlanServiceResult<T> inUse() {
        return new MembershipPlanServiceResult<>(MembershipPlanServiceStatus.IN_USE, null);
    }
}
