package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class OutStateException extends RuntimeException{
    private int errorCode;

    public OutStateException() {
        super(ResponseMsg.OUT_STATE);
        this.errorCode = StatusCode.FORBIDDEN;
    }
}
