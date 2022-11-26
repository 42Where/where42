package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class RegisteredFriendException extends Exception {
    private int errorCode;

    public RegisteredFriendException() {
        super(ResponseMsg.REGISTERED_GROUP_FRIEND);
        this.errorCode = StatusCode.CONFLICT;
    }
}
