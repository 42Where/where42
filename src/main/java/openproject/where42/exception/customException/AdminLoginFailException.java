package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class AdminLoginFailException extends RuntimeException{
	private int errorCode;

	public AdminLoginFailException() {
		super(ResponseMsg.ADMIN_FAIL);
		this.errorCode = StatusCode.UNAUTHORIZED;
	}

}
