package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     멤버가 커스텀 그룹 생성 및 이름 변경 희망 시 자신이 가진 커스텀 그룹의 이름과 중복된 이름으로 신청할 경우 발생
 *     error code: 409
 * </pre>
 * @see openproject.where42.group.GroupService#createCustomGroup(String, Member) 커스텀 그룹 생성
 * @see openproject.where42.group.GroupService#updateGroupName(Long, String) 커스텀 그룹 이름 변경
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class DuplicateGroupNameException extends RuntimeException {
    private int errorCode;

    public DuplicateGroupNameException() {
        super(ResponseMsg.DUPLICATE_GROUP_NAME);
        this.errorCode = StatusCode.CONFLICT;
    }
}
