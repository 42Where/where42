package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     없는 그룹 삭제 등 사용자의 잘못된 요청시 발생
 *     error code: 400
 * </pre>
 * @see openproject.where42.group.GroupApiController
 * @see openproject.where42.group.GroupService
 * @see openproject.where42.groupFriend.GroupFriendService
 * @since 2.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class BadRequestException extends RuntimeException {
    private int errorCode;

    public BadRequestException() {
        super(ResponseMsg.BAD_REQUEST);
        this.errorCode = StatusCode.BAD_REQUEST;
    }
}