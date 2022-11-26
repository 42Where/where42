package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class UnregisteredMemberException extends Exception {
    private int errorCode;
    private Seoul42 seoul42;

    public UnregisteredMemberException(Seoul42 seoul42) {
        super(ResponseMsg.UNREGISTERED);
        this.errorCode = StatusCode.UNAUTHORIZED;
        this.seoul42 = seoul42;
    }
}
