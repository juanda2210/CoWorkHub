package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.MemberServiceStatus;

public record MemberServiceResult<T>(MemberServiceStatus status, T value) {

    public static <T> MemberServiceResult<T> ok(T value) {
        return new MemberServiceResult<>(MemberServiceStatus.OK, value);
    }

    public static <T> MemberServiceResult<T> notFound() {
        return new MemberServiceResult<>(MemberServiceStatus.NOT_FOUND, null);
    }

    public static <T> MemberServiceResult<T> planNotFound() {
        return new MemberServiceResult<>(MemberServiceStatus.PLAN_NOT_FOUND, null);
    }

    public static <T> MemberServiceResult<T> documentTaken() {
        return new MemberServiceResult<>(MemberServiceStatus.DOCUMENT_TAKEN, null);
    }

    public static <T> MemberServiceResult<T> emailTaken() {
        return new MemberServiceResult<>(MemberServiceStatus.EMAIL_TAKEN, null);
    }

    public static <T> MemberServiceResult<T> hasBookings() {
        return new MemberServiceResult<>(MemberServiceStatus.HAS_BOOKINGS, null);
    }

    public static <T> MemberServiceResult<T> usernameTaken() {
        return new MemberServiceResult<>(MemberServiceStatus.USERNAME_TAKEN, null);
    }
}
