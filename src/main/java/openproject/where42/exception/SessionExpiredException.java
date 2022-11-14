package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class SessionExpiredException extends RuntimeException{
    private int errorCode;

    public SessionExpiredException() {
        super(ResponseMsg.NO_SESSION);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}
