package com.therapy.nest.shared.enums;

import lombok.Getter;

@Getter
public enum GeneralConfigurationEnum {
    MAX_LOGIN_ATTEMPTS("2"),
    LOCKOUT_TIME("1"),
    MAXIMUM_THROTTLE_TOKENS("20"),
    THROTTLE_REFILL_TOKENS("5"),
    THROTTLE_REFILL_DURATION("3");

    private final String value;

    GeneralConfigurationEnum(String value) {
        this.value = value;
    }

}
