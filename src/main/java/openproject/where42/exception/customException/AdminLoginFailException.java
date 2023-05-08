package openproject.where42.exception.customException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.admin.AdminService;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

/**
 * <pre>
 *     관리자 로그인 시 id - pwd 불일치 시 발생
 *     error code: 401
 * </pre>
 * @see AdminService#adminLogin(String, String)
 * @since 1.0
 * @author hyunjcho
 */
@Getter
@Slf4j
public class AdminLoginFailException extends RuntimeException{
	private int errorCode;

	public AdminLoginFailException() {
		super(ResponseMsg.ADMIN_FAIL);
		this.errorCode = StatusCode.UNAUTHORIZED;
	}
}
