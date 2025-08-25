package com.therapy.nest.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionDto {
    private String name;
    private String displayName;
    private String permissionGroup;
    private boolean isGlobal;
}
