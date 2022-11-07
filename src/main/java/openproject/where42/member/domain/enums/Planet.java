package openproject.where42.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Planet {
    gaepo(1), seocho(2);

    Planet(Integer value) {
        this.value = value;
    }

    private final Integer value;

    @JsonValue
    public int getValue() {
        return this.value;
    }

}
