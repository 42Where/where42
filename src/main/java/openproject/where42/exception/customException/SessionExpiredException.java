package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class SessionExpiredException extends RuntimeException{
    private int errorCode;

    public SessionExpiredException() {
        super(ResponseMsg.NO_SESSION);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}
