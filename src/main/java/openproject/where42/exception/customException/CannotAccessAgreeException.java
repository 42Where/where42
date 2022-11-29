package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class CannotAccessAgreeException extends RuntimeException {
    private int errorCode;

    public CannotAccessAgreeException() {
        super(ResponseMsg.CANNOT_ACCESS_AGREE);
        this.errorCode = StatusCode.CONFLICT;
    }
}
