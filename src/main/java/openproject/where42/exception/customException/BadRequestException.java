package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
@Slf4j
public class BadRequestException extends RuntimeException {
	private int errorCode;

	public BadRequestException() {
		super(ResponseMsg.BAD_REQUEST);
		log.info("************** [BadRequestException]이 발생하였습니다. **************");
		this.errorCode = StatusCode.BAD_REQUEST;
	}
}