package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.AdditionalServiceServiceStatus;

public record AdditionalServiceServiceResult<T>(AdditionalServiceServiceStatus status, T value) {

    public static <T> AdditionalServiceServiceResult<T> ok(T value) {
        return new AdditionalServiceServiceResult<>(AdditionalServiceServiceStatus.OK, value);
    }

    public static <T> AdditionalServiceServiceResult<T> notFound() {
        return new AdditionalServiceServiceResult<>(AdditionalServiceServiceStatus.NOT_FOUND, null);
    }

    public static <T> AdditionalServiceServiceResult<T> nameTaken() {
        return new AdditionalServiceServiceResult<>(AdditionalServiceServiceStatus.NAME_TAKEN, null);
    }

    public static <T> AdditionalServiceServiceResult<T> inUse() {
        return new AdditionalServiceServiceResult<>(AdditionalServiceServiceStatus.IN_USE, null);
    }
}
