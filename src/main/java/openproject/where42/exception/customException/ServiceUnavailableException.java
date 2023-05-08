package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;


/**
 * <pre>
 *     멤버가 수동 자리 설정을 하려고 하였으나 해당 멤버의 하네 정보를 불러 올 수 없는 경우 발생
 *     error code: 503
 * </pre>
 * @since 2.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class ServiceUnavailableException extends Exception {
    private int errorCode;

    public ServiceUnavailableException() {
        super(ResponseMsg.SERVICE_UNAVAILABLE);
        this.errorCode = StatusCode.SERVICE_UNAVAILABLE;
    }
}
