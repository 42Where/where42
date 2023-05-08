package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 *     멤버가 수동 자리 설정을 하려고 하였으나 아이맥에 로그인 되어 있는 경우 발생
 *     error code: 400
 * </pre>
 * @see openproject.where42.member.MemberService#checkLocate(HttpServletRequest, String) 수동 자리 설정 가능 여부 조회
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class TakenSeatException extends Exception{
    private int errorCode;

    public TakenSeatException() {
        super(ResponseMsg.TAKEN_SEAT);
        this.errorCode = StatusCode.CONFLICT;
    }
}
