package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.RoomServiceStatus;

public record RoomServiceResult<T>(RoomServiceStatus status, T value) {

    public static <T> RoomServiceResult<T> ok(T value) {
        return new RoomServiceResult<>(RoomServiceStatus.OK, value);
    }

    public static <T> RoomServiceResult<T> notFound() {
        return new RoomServiceResult<>(RoomServiceStatus.NOT_FOUND, null);
    }

    public static <T> RoomServiceResult<T> locationNotFound() {
        return new RoomServiceResult<>(RoomServiceStatus.LOCATION_NOT_FOUND, null);
    }

    public static <T> RoomServiceResult<T> equipmentNotFound() {
        return new RoomServiceResult<>(RoomServiceStatus.EQUIPMENT_NOT_FOUND, null);
    }

    public static <T> RoomServiceResult<T> nameTaken() {
        return new RoomServiceResult<>(RoomServiceStatus.NAME_TAKEN, null);
    }

    public static <T> RoomServiceResult<T> inUse() {
        return new RoomServiceResult<>(RoomServiceStatus.IN_USE, null);
    }

    public static <T> RoomServiceResult<T> invalidTimeRange() {
        return new RoomServiceResult<>(RoomServiceStatus.INVALID_TIME_RANGE, null);
    }
}