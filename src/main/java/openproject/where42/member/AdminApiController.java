package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.background.BackgroundService;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.flashData.FlashDataRepository;
import openproject.where42.member.dto.AdminInfo;
import openproject.where42.token.TokenRepository;
import openproject.where42.util.Define;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class AdminApiController {
	private final BackgroundService backgroundService;
	private final FlashDataRepository flashDataRepository;
	private final TokenRepository tokenRepository;
	private final MemberService memberService;

	// 관리자 로그인
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/login")
	public ResponseEntity adminLogin(HttpSession session, @RequestBody AdminInfo admin) {
		Long id = memberService.adminLogin(admin.getName(), admin.getPasswd());
		session.setAttribute("id", id);
		session.setMaxInactiveInterval(30 * 60);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ADMIN_LOGIN_SUCCESS), HttpStatus.OK);
	}

	@PostMapping(Define.WHERE42_VERSION_PATH + "/hane")
	public ResponseEntity insertHane(HttpServletRequest req) {
		if (!memberService.findAdminBySession(req))
			throw new SessionExpiredException();
		tokenRepository.insertHane();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.HANE_SUCCESS), HttpStatus.OK);
	}
	// 여기에 어드민 멤버 레벨 조건 넣기

	@GetMapping(Define.WHERE42_VERSION_PATH + "/incluster") // 서버 실행 시 자동 실행 방법..?
	public ResponseEntity findAllInClusterCadet(HttpServletRequest req) {
		if (!memberService.findAdminBySession(req))
			throw new SessionExpiredException();
		backgroundService.updateAllInClusterCadet();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IN_CLUSTER), HttpStatus.OK);
	}

	@GetMapping(Define.WHERE42_VERSION_PATH + "/image")
	public ResponseEntity getAllCadetImages(HttpServletRequest req) {
		if (!memberService.findAdminBySession(req))
			throw new SessionExpiredException();
		backgroundService.getAllCadetImages(); // 에러 처리 확인
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
	}

	@DeleteMapping(Define.WHERE42_VERSION_PATH + "/flash")
	public ResponseEntity resetFlash(HttpServletRequest req) {
		if (!memberService.findAdminBySession(req))
			throw new SessionExpiredException();
		flashDataRepository.resetFlash();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.RESET_FLASH), HttpStatus.OK);
	}

	// 멤버 삭제
	@DeleteMapping(Define.WHERE42_VERSION_PATH + "/member/{name}")
	public ResponseEntity deleteMember(@PathVariable(name = "name") String name, HttpServletRequest req) {
		if (!memberService.findAdminBySession(req))
			throw new SessionExpiredException();
//        Administrator admin = memberService.findBySession(req);
//        if (admin == null || admin.getLevel() != MemberLevel.administrator) // 관리자 계정 필요, 시큐리티에서 할 수 있는 방법은?
		memberService.deleteMember(name);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_MEMBER), HttpStatus.OK);
	}

	// 관리자 로그아웃
	@GetMapping(Define.WHERE42_VERSION_PATH + "/admin/logout")
	public ResponseEntity adminLogout(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session != null)
			session.invalidate();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ADMIN_LOGOUT_SUCCESS), HttpStatus.OK);
	}
}
