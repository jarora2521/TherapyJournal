package com.therapy.nest.uaa.enums;

import lombok.Getter;

@Getter
public enum PermissionGroupEnum {
    UAA_PERMISSIONS("UAA PERMISSIONS"),
    ATTACHMENT_PERMISSIONS("ATTACHMENT PERMISSIONS");

    private final String permissionGroup;

    PermissionGroupEnum(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }
}
