package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class CookieExpiredException extends RuntimeException{
    private int errorCode;

    public CookieExpiredException() {
        super(ResponseMsg.NO_COOKIE);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}
