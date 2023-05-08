package openproject.where42.member.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 위치 정보 중 개포/서초/외출/퇴근 표현 enum 클래스
 * gaepo(1): 개포
 * seocho(2): 서초
 * rest(3): 외출
 * error(4): 24hane 오류(ver2에 추가)
 * @version 2.0
 * @author sunghkim
 */
public enum Planet {
    gaepo(1), seocho(2), rest(3), error(4);

    Planet(Integer value) {
        this.value = value;
    }

    private final Integer value;

    @JsonValue
    public int getValue() {
        return this.value;
    }

}
