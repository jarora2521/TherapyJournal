package com.therapy.nest.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoleDto {
    private String uuid;
    private String name;
    private List<String> permissions;
}
