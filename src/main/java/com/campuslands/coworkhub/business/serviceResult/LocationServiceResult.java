package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.LocationServiceStatus;

public record LocationServiceResult<T>(LocationServiceStatus status, T value) {

    public static <T> LocationServiceResult<T> ok(T value) {
        return new LocationServiceResult<>(LocationServiceStatus.OK, value);
    }

    public static <T> LocationServiceResult<T> notFound() {
        return new LocationServiceResult<>(LocationServiceStatus.NOT_FOUND, null);
    }

    public static <T> LocationServiceResult<T> invalidSchedule() {
        return new LocationServiceResult<>(LocationServiceStatus.INVALID_SCHEDULE, null);
    }

    public static <T> LocationServiceResult<T> inUse() {
        return new LocationServiceResult<>(LocationServiceStatus.IN_USE, null);
    }
}
