package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class DefaultGroupNameException extends Exception {
    private int errorCode;

    public DefaultGroupNameException() {
        super(ResponseMsg.DEFAULT_GROUP_NAME);
        this.errorCode = StatusCode.CONFLICT;
    }
}