package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.BookingServiceStatus;

public record BookingServiceResult<T>(BookingServiceStatus status, T value) {

    public static <T> BookingServiceResult<T> ok(T value) {
        return new BookingServiceResult<>(BookingServiceStatus.OK, value);
    }

    public static <T> BookingServiceResult<T> notFound() {
        return new BookingServiceResult<>(BookingServiceStatus.NOT_FOUND, null);
    }

    public static <T> BookingServiceResult<T> memberNotFound() {
        return new BookingServiceResult<>(BookingServiceStatus.MEMBER_NOT_FOUND, null);
    }

    public static <T> BookingServiceResult<T> memberNotActive() {
        return new BookingServiceResult<>(BookingServiceStatus.MEMBER_NOT_ACTIVE, null);
    }

    public static <T> BookingServiceResult<T> roomNotFound() {
        return new BookingServiceResult<>(BookingServiceStatus.ROOM_NOT_FOUND, null);
    }

    public static <T> BookingServiceResult<T> roomInactive() {
        return new BookingServiceResult<>(BookingServiceStatus.ROOM_INACTIVE, null);
    }

    public static <T> BookingServiceResult<T> capacityExceeded() {
        return new BookingServiceResult<>(BookingServiceStatus.CAPACITY_EXCEEDED, null);
    }

    public static <T> BookingServiceResult<T> invalidDuration() {
        return new BookingServiceResult<>(BookingServiceStatus.INVALID_DURATION, null);
    }

    public static <T> BookingServiceResult<T> outsideOpeningHours() {
        return new BookingServiceResult<>(BookingServiceStatus.OUTSIDE_OPENING_HOURS, null);
    }

    public static <T> BookingServiceResult<T> tooSoon() {
        return new BookingServiceResult<>(BookingServiceStatus.TOO_SOON, null);
    }

    public static <T> BookingServiceResult<T> bookingLimitReached() {
        return new BookingServiceResult<>(BookingServiceStatus.BOOKING_LIMIT_REACHED, null);
    }

    public static <T> BookingServiceResult<T> roomNotAvailable() {
        return new BookingServiceResult<>(BookingServiceStatus.ROOM_NOT_AVAILABLE, null);
    }

    public static <T> BookingServiceResult<T> serviceNotFound() {
        return new BookingServiceResult<>(BookingServiceStatus.SERVICE_NOT_FOUND, null);
    }

    public static <T> BookingServiceResult<T> notPending() {
        return new BookingServiceResult<>(BookingServiceStatus.NOT_PENDING, null);
    }

    public static <T> BookingServiceResult<T> serviceNotInBooking() {
        return new BookingServiceResult<>(BookingServiceStatus.SERVICE_NOT_IN_BOOKING, null);
    }

    public static <T> BookingServiceResult<T> invalidTransition() {
        return new BookingServiceResult<>(BookingServiceStatus.INVALID_TRANSITION, null);
    }

    public static <T> BookingServiceResult<T> cancellationNotAllowed() {
        return new BookingServiceResult<>(BookingServiceStatus.CANCELLATION_NOT_ALLOWED, null);
    }
}