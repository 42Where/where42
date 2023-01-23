package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
@Slf4j
public class NotFoundException extends RuntimeException {
    private int errorCode;

    public NotFoundException() {
        super(ResponseMsg.NOT_FOUND);
        log.info("************** [NotFoundException]이 발생하였습니다. **************");
        this.errorCode = StatusCode.NOT_FOUND;
    }
}