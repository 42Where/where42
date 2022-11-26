package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
@Getter
public class TakenSeatException extends Exception{
    private int errorCode;

    public TakenSeatException() {
        super(ResponseMsg.TAKEN_SEAT);
        this.errorCode = StatusCode.CONFLICT;
    }
}
