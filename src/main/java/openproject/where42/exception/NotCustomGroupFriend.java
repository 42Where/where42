package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;

@Getter
public class NotCustomGroupFriend extends RuntimeException {
    private int errorCode;

    public NotCustomGroupFriend() {
        super(ResponseMsg.NOT_CUSTOM_GROUP_FRIEND);
        this.errorCode = StatusCode.BAD_REQUEST;
    }
}
