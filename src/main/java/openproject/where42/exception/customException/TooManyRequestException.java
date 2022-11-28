package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class TooManyRequestException extends RuntimeException {
    private int errorCode;

    public TooManyRequestException() {
        super(ResponseMsg.TOO_MANY_REQUEST);
        this.errorCode = StatusCode.TOO_MANY_REQUEST;
    }
}
