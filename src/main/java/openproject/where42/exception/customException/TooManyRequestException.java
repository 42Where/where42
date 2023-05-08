package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     42Api를 1초에 2회 혹은 10분에 1,200회 이상 조회하였거나, 기타 등등의 이유로 42api 요청에 대해 오류가 반환된 경우 발생
 *     error code: 400
 * </pre>
 * @see openproject.where42.api.ApiService
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class TooManyRequestException extends RuntimeException {
    private int errorCode;

    public TooManyRequestException() {
        super(ResponseMsg.TOO_MANY_REQUEST);
        this.errorCode = StatusCode.TOO_MANY_REQUEST;
    }
}
