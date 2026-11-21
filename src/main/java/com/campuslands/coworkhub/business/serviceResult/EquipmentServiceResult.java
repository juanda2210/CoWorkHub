package com.campuslands.coworkhub.business.serviceResult;

import com.campuslands.coworkhub.business.serviceStatus.EquipmentServiceStatus;

public record EquipmentServiceResult<T>(EquipmentServiceStatus status, T value) {

    public static <T> EquipmentServiceResult<T> ok(T value) {
        return new EquipmentServiceResult<>(EquipmentServiceStatus.OK, value);
    }

    public static <T> EquipmentServiceResult<T> notFound() {
        return new EquipmentServiceResult<>(EquipmentServiceStatus.NOT_FOUND, null);
    }

    public static <T> EquipmentServiceResult<T> nameTaken() {
        return new EquipmentServiceResult<>(EquipmentServiceStatus.NAME_TAKEN, null);
    }

    public static <T> EquipmentServiceResult<T> inUse() {
        return new EquipmentServiceResult<>(EquipmentServiceStatus.IN_USE, null);
    }
}
