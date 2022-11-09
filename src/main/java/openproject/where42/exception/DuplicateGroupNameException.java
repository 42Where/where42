package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class DuplicateGroupNameException extends RuntimeException {
    private int errorCode;

    public DuplicateGroupNameException() {
        super(ResponseMsg.DUPLICATE_GROUP_NAME);
        this.errorCode = StatusCode.DUPLICATE_GROUP_NAME;
    }
}
