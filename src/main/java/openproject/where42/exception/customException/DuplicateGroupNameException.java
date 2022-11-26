package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class DuplicateGroupNameException extends Exception {
    private int errorCode;

    public DuplicateGroupNameException() {
        super(ResponseMsg.DUPLICATE_GROUP_NAME);
        this.errorCode = StatusCode.CONFLICT;
    }
}
