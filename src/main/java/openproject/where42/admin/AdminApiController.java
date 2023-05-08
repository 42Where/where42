package openproject.where42.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.background.BackgroundService;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.flashData.FlashDataRepository;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.dto.AdminInfo;
import openproject.where42.token.TokenRepository;
import openproject.where42.util.Define;
import openproject.where42.util.SearchCadet;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

/**
 * <pre>
 *     관리자용 API 컨트롤러 클래스
 *     보안 강화를 위해 member level 조회하는 로직을 추가하면 좋을 것 같음
 * </pre>
 * @version 2.0
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminApiController {
	private final BackgroundService backgroundService;
	private final FlashDataRepository flashDataRepository;
	private final TokenRepository tokenRepository;
	private final AdminRepository adminRepository;
	private final AdminService adminService;
	private final AdminApiService adminApiService;
	private final MemberRepository memberRepository;

	/**
	 * 관리자 로그인 및 세션 생성
	 * @param session 관리자 세션 생성용 세션
	 * @param admin 관리자 로그인용 Id 및 PWD
	 * @return 로그인 성공
	 * @throws openproject.where42.exception.customException.AdminLoginFailException 일치하는 관리자 없을 경우 401 예외 throw
	 * @see AdminInfo
	 * @see AdminService#adminLogin(String, String) 관리자 로그인
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/login")
	public ResponseEntity adminLogin(HttpSession session, @RequestBody AdminInfo admin) {
		adminService.adminLogin(admin.getName(), admin.getPasswd());
		session.setAttribute("name", admin.getName());
		session.setMaxInactiveInterval(30 * 60);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ADMIN_LOGIN_SUCCESS), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     관리자용 42api application secret_id DB 갱신
	 *     서버를 중단하지 않고 갱신하기 위해 DB에 갱신 및 해당 DB를 호출하여 사용
	 *     아래 DB 갱신용 메소드들도 같으며 원활한 운영을 위해 주기별로 업데이트 해야함
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @param secret 갱신할 secret id
	 * @return 갱신 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#findAdminBySession(HttpServletRequest) 관리자 세션 조회
	 * @see AdminRepository#insertAdminSecret(String) 관리자 api app secret_id DB 갱신
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/secret/admin")
	public ResponseEntity updateAdminServerSecret(HttpServletRequest req, @RequestBody Map<String, String> secret) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		adminRepository.insertAdminSecret(secret.get("secret"));
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SECRET_UPDATE_SUCCESS), HttpStatus.OK);
	}

	/**
	 * 사용자용 42api application secret_id DB 갱신
	 * @param req 세션 확인용 HttpServletRequest
	 * @param secret 갱신할 secret id
	 * @return 갱신 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#findAdminBySession(HttpServletRequest) 관리자 세션 조회
	 * @see TokenRepository#insertSecret(String) 사용자 api app secret_id DB 갱신
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/secret/member")
	public ResponseEntity updateServerSecret(HttpServletRequest req, @RequestBody Map<String, String> secret) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		tokenRepository.insertSecret(secret.get("secret"));
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SECRET_UPDATE_SUCCESS), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     관라자 42api application code 획득용 주소 front_end 반환
	 *     사용자와 달리 관리자의 경우 retry 하지 않음
	 * </pre>
	 * @return 42api code 획득용 주소
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/auth/admin")
	public String adminAuthLogin() {
		return "redirect url";
	}

	/**
	 * <pre>
	 *     사용자 42api application code 획득용 주소 front_end 반환
	 *     code 발급 후 이루어지는 redirection이 프론트에서 진행되어야 하여,
	 *     Too Many Request 에러에 대응하기 위해 front_end에서 해당 링크를 조회하는 시점으로 제어
	 *     오류 발생 시 1초 간격으로 최대 3번 재시도
	 * </pre>
	 * @return 42api code 획득용 주소
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Retryable(maxAttempts = 3, backoff = @Backoff(1000))
	@GetMapping(Define.WHERE42_VERSION_PATH + "/auth/login")
	public String authLogin() {
		return "redirect url";
	}

	/**
	 * <pre>
	 *     상기 메소드가 3번 실패 시 실행되는 메서드
	 *     실제 익셉션은 e.getMessage 메소드를 통해 로그로 남기고
	 *     throw는 TooManyRequestException으로 통일
	 * </pre>
	 * @param e 던져진 e
	 * @throws TooManyRequestException 42api 에러 발생 시 429 예외 throw
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Recover
	public String fallback(RuntimeException e) {
		throw new TooManyRequestException();
	}

	/**
	 * 관리자 access token DB 저장
	 * @param req 세션 확인용 HttpServletRequest
	 * @param code 42api 요청용 code
	 * @return 저장 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#findAdminBySession(HttpServletRequest) 관리자 세션 조회
	 * @see AdminApiService#getAdminOAuthToken(String, String) 관리자 OAuth Token 발급
	 * @see AdminRepository#saveAdmin(String, OAuthToken) 관리자 OAuth Token 저장
	 * @since 1.0
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/auth/admin/token")
	public ResponseEntity insertAdminToken(HttpServletRequest req, @RequestBody Map<String, String> code) {
		log.info("[insertAdminToken] Admin Token을 주입합니다.");
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		OAuthToken oAuthToken = adminApiService.getAdminOAuthToken(adminRepository.callAdminSecret(), code.get("code"));
		adminRepository.saveAdmin("admin", oAuthToken);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ADMIN_TOKEN_SUCCESS), HttpStatus.OK);
	}

	/**
	 * 24hane access token DB 저장. 토큰 만료 시 24hane 담당자(현재 joopark)에게 연락하여 갱신
	 * @param req 세션 확인용 HttpServletRequest
	 * @param token 갱신할 토큰
	 * @return 갱신 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#findAdminBySession(HttpServletRequest) 관리자 세션 조회
	 * @see AdminRepository#insertHane(String) 24hane 토큰 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/hane")
	public ResponseEntity insertHane(HttpServletRequest req, @RequestBody Map<String, String> token) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		adminRepository.insertHane(token.get("token"));
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.HANE_SUCCESS), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     클러스터 아이맥에 로그인 해 있는 모든 카뎃들의 정보 갱신
	 *     서버가 중단되었거나 flash 데이터를 초기화 정확한 로그인 정보 추적을 위해 꼭 진행해야 함
	 *     로그인한 카뎃이 많을 경우 서버 운영에 지장이 갈 수 있으므로 카뎃이 많이 출근하지 않은 시간대에 진행하는 것을 권장
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @return 정보 저장 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see BackgroundService#updateAllInClusterCadet() 클러스터 아이맥 로그인 정보 갱신
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/admin/incluster")
	public ResponseEntity findAllInClusterCadet(HttpServletRequest req) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		backgroundService.updateAllInClusterCadet();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IN_CLUSTER), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     모든 플래시 데이터 초기화
	 *     멤버나 친구로 등록되지 않은 카뎃의 경우 불필요한 정보이기 때문에 유휴 정보 최소화를 위해
	 *     2주 등 적절한 기간에 한 번씩 주기적으로 초기화 할 것을 권장
	 *     초기화를 진행할 경우 상기 "incluster" api를 꼭 실행하여야 정확한 로그인 정보 추적이 가능함
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @return 초기화 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see FlashDataRepository#resetFlash() 플래시 데이터 초기화
	 * @since 1.0
	 * @author hyunjcho
	 */
	@DeleteMapping(Define.WHERE42_VERSION_PATH + "/admin/flash")
	public ResponseEntity resetFlash(HttpServletRequest req) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		flashDataRepository.resetFlash();
		log.info("[reset-flash] flash data 초기화를 완료하였습니다.");
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.RESET_FLASH), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     블랙홀 인원 제외 모든 카뎃들의 이미지 url 갱신
	 *     새로운 기수가 들어오는 경우 꼭 진행해 주어야 함
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @return 이미지 갱신 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see BackgroundService#getAllCadetImages() 블랙홀 제외 모든 카뎃들의 이미지 url 갱신
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/image")
	public ResponseEntity getAllCadetImages(HttpServletRequest req) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		backgroundService.getAllCadetImages();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
	}

	/**
	 * <pre>
	 *     모든 카뎃들의 피신 시작일 삽입
	 *     ver2에 통계를 위해 새롭게 추가된 DB 컬럼으로, 기존 회원들의 피신 시작일 삽입
	 *     ver2 업데이트 이후 가입자의 경우 가입시 해당 정보를 넣고 있으나 누락이 발생할 경우 해당 api로 삽입 가능
	 *     1회성 api로 성공에 대한 response 만들지 않음
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @return 피신 시작일 삽입 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#getSignUpDate() 피신 시작일 삽입
	 * @since 2.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/admin/createdAt")
	public ResponseEntity getAllCadetCreateAt(HttpServletRequest req) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		adminService.getSignUpDate();
		return null;
	}

	/**
	 * <pre>
	 *     멤버 삭제시 사용하며, 그룹 친구 정보 등 모두 삭제 됨
	 *     현재 어디있니 사이트 내부에서 사용자가 회원을 탈퇴할 수는 없으나,
	 *     만약 그럼에도 불구하고 희망하는 경우 해당 api를 사용하여 삭제
	 *     개인정보 제공 동의를 3년으로 하고 있기 때문에 가입 이후 3년이 지난 시점에 동의 여부를 다시 묻거나, 일괄 삭제 해야함
	 * </pre>
	 * @param req 세션 확인용 HttpServletRequest
	 * @param name 삭제할 멤버 이름
	 * @return 삭제 성공
	 * @throws SessionExpiredException 세션 만료 시 401 예외 throw
	 * @see AdminService#deleteMember(String) 멤버 삭제
	 * @since 1.0
	 * @author hyunjcho
	 */
	@DeleteMapping(Define.WHERE42_VERSION_PATH + "/admin/member")
	public ResponseEntity deleteMember(HttpServletRequest req, @RequestParam(name = "name") String name) {
		if (!adminService.findAdminBySession(req))
			throw new SessionExpiredException();
		adminService.deleteMember(name);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_MEMBER), HttpStatus.OK);
	}

	/**
	 * "어디있니" 검색 이스터 에그용!
	 * @return 관리자 정보 반환
	 * @see AdminRepository#findAllAdmin() 관리자 DB 정보 조회
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/search/where42")
	public ArrayList<SearchCadet> searchWhere42Info() {
		return adminService.where42(adminRepository.findAllAdmin());
	}

	/**
	 * 관리자 로그아웃(세션 삭제)
	 * @param req 세션 확인용 HttpServletRequest
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/admin/logout")
	public ResponseEntity adminLogout(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session != null)
			session.invalidate();
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ADMIN_LOGOUT_SUCCESS), HttpStatus.OK);
	}
}
