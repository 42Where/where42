package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class OutStateException extends RuntimeException{
    private int errorCode;

    public OutStateException() {
        super(ResponseMsg.OUT_STATE);
        this.errorCode = StatusCode.FORBIDDEN;
    }
}
