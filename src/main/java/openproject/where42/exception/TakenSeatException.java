package openproject.where42.exception;

import lombok.Getter;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
@Getter
public class TakenSeatException extends RuntimeException{
    private int errorCode;

    public TakenSeatException() {
        super(ResponseMsg.TAKEN_SEAT);
        this.errorCode = StatusCode.TAKEN_SEAT;
    }
}
