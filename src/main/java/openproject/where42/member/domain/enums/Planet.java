package openproject.where42.member.domain.enums;

import lombok.Getter;

@Getter
public enum Planet {
    gaepo(1), seocho(2);

    Planet(Integer value) {
        this.value = value;
    }

    private final Integer value;

}
