package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     멤버 가입 시 기본적으로 생성되는 기본 및 즐겨찾기 그룹에 대해 다른 이름이 들어올 경우 발생
 *     error code: 409
 * </pre>
 * @see openproject.where42.group.GroupService#createDefaultGroup(Member, String) 기본 그룹 생성
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class DefaultGroupNameException extends RuntimeException {
    private int errorCode;

    public DefaultGroupNameException() {
        super(ResponseMsg.DEFAULT_GROUP_NAME);
        this.errorCode = StatusCode.CONFLICT;
    }
}