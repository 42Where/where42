package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 *     멤버가 수동 자리 설정을 하려고 하였으나 클러스터 외부에 있는 경우 발생
 *     error code: 409
 *     checked exception으로 발생 후에도 초기화 된 정보는 저장됨
 * </pre>
 * @see openproject.where42.member.MemberService#checkLocate(HttpServletRequest, String) 수동 자리 설정 가능 여부 조회
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class OutStateException extends Exception {
    private int errorCode;

    public OutStateException() {
        super(ResponseMsg.OUT_STATE);
        this.errorCode = StatusCode.FORBIDDEN;
    }
}
