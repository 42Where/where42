package openproject.where42.member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "손님"),
    MEMBER("ROLE_MEMBER", "멤버");

    private final String key;
    private final String title;
}
