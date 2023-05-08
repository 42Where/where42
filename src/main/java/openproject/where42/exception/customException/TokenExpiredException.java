package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     토큰이 만료된 경우 발생
 *     error code: 401
 * </pre>
 * @see openproject.where42.member.MemberApiController
 * @since 1.0
 * @author sunghkim
 */
@Getter
@Slf4j
public class TokenExpiredException extends RuntimeException {
    private int errorCode;

    public TokenExpiredException() {
        super(ResponseMsg.NO_TOKEN);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}
