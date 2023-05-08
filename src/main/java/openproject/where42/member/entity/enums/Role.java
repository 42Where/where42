package openproject.where42.member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 스프링 시큐리티용으로 만들었으나 시큐리티 설정을 많이 하지 않음..
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "손님"),
    MEMBER("ROLE_MEMBER", "멤버");

    private final String key;
    private final String title;
}
