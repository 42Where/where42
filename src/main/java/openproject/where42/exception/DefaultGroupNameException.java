package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class DefaultGroupNameException extends RuntimeException {
    private int errorCode;

    public DefaultGroupNameException() {
        super(ResponseMsg.DEFAULT_GROUP_NAME);
        this.errorCode = StatusCode.CONFLICT;
    }
}