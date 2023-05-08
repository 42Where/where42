package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.BadRequestException;
import openproject.where42.token.TokenService;
import openproject.where42.util.Define;
import openproject.where42.exception.customException.RegisteredFriendException;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.ResponseWithData;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 친구 관련 API 컨트롤러 클래스
 * @version 2.0
 * @see openproject.where42.group
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GroupFriendApiController {

	private final GroupFriendService groupFriendService;
	private final GroupFriendRepository groupFriendRepository;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final TokenService tokenService;
	private final ApiService apiService;

	/**
	 * <pre>
	 *     친구 생성 api
	 *     SearchApiController와 연계되어 있으며 검색 후 친구 등록 됨
	 *     멤버 생성 시 함께 생성 된 기본 그룹에 추가됨
	 * </pre>
	 * @param req 멤버 세션 확인용 HttpServletRequest
	 * @param res 멤버 토큰 확인용 HttpServletResponse
	 * @param key 멤버 토큰 확인용 쿠키값
	 * @param friendName 등록할 친구 이름
	 * @param img 등록할 친구 사진 주소, 42api 주소가 "null"일 경우 프론트에서 웨얼이 사진 주소로 대체해서 들어옴
	 * @return 생성된 친구 아이디 반환
	 * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
	 * @throws openproject.where42.exception.customException.UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
	 * @throws RegisteredFriendException 이미 등록된 친구일 경우 409 예외 throw
	 * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
	 * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰을 통한 멤버 조회
	 * @see openproject.where42.member.MemberRepository#checkFriendByMemberIdAndName(Long, String) 이미 등록된 친구인지 확인
	 * @see openproject.where42.groupFriend.GroupFriendService#saveFriend(String, String, String, Long)  친구 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/groupFriend")
	public ResponseEntity createFriend(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestParam String friendName, @RequestParam String img) {
		String token42 = tokenService.findAccessToken(res, key);
		Member member = memberService.findBySessionWithToken(req, token42);
		if (memberRepository.checkFriendByMemberIdAndName(member.getId(), friendName))
			throw new RegisteredFriendException();
		Seoul42 friend = apiService.getUserInfo(token42, friendName);
		Long friendId = groupFriendService.saveFriend(friendName, img, friend.getCreated_at(), member.getDefaultGroupId());
		log.info("[friend] \"{}\"님이 \"{}\"님을 친구 추가 하였습니다", member.getName(), friendName);
		return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_GROUP_FRIEND, friendId), HttpStatus.CREATED);
	}

	/**
	 * <pre>
	 *     인자로 받은 해당 그룹에 포함되지 않은 친구 이름 목록 조회 api
	 *     멤버가 등록한 친구 전체 목록과 비교를 위해 멤버 유효성 검사 진행
	 * </pre>
	 * @param req 멤버 세션 확인용 HttpServletRequest
	 * @param res 멤버 토큰 확인용 HttpServletResponse
	 * @param key 멤버 토큰 확인용 쿠키값
	 * @param groupId 멤버가 생성한 커스텀 그룹 아이디
	 * @return 해당 그룹에 포함되지 않은 친구 이름 string 리스트로 반환
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
	 * @throws openproject.where42.exception.customException.UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
	 * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
	 * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰을 통한 멤버 조회
	 * @see openproject.where42.groupFriend.GroupFriendRepository#notIncludeFriendByGroup(Member, Long) 해당 그룹에 포함되지 않은 친구 조회
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/notIncludes/group/{groupId}")
	public List<String> getNotIncludeGroupFriendNames(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @PathVariable("groupId") Long groupId) {
		String token42 = tokenService.findAccessToken(res, key);
		Member member = memberService.findBySessionWithToken(req, token42);
		List<String> ret = groupFriendRepository.notIncludeFriendByGroup(member, groupId);
		if (ret == null)
			throw new BadRequestException();
		return ret;
	}

	/**
	 * <pre>
	 *     인자로 받은 해당 그룹에, 인자로 받은 친구들 일괄 추가 api
	 *     groupId는 유니크성으로 요청 멤버에 대한 유효성 검사는 진행하지 않음
	 * </pre>
	 * @param groupId 친구들을 추가하고자 하는 그룹 아이디
	 * @param friendNames 추가하고자 하는 친구들 이름 목록
	 * @return 그룹 생성 성공 반환
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @see openproject.where42.groupFriend.GroupFriendService#addFriendsToGroup(List, Long) 친구 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/notIncludes/group/{groupId}")
	public ResponseEntity addFriendsToGroup(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.addFriendsToGroup(friendNames, groupId);
		log.info("[group] {}번 그룹에 친구가 추가되었습니다.", groupId);
		return new ResponseEntity(Response.res(StatusCode.CREATED, ResponseMsg.ADD_FRIENDS_TO_GROUP), HttpStatus.CREATED);
	}

	/**
	 * <pre>
	 *     인자로 받은 해당 그룹에 포함된 친구 이름 목록 조회 api
	 *     groupId 유니크성으로 요청 멤버에 대한 유효성 검사는 진행하지 않음
	 * </pre>
	 * @param groupId 친구 목록을 조회하고자 하는 그룹 아이디
	 * @return 해당 그룹에 포함된 친구들 이름 목록 반환
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @see openproject.where42.groupFriend.GroupFriendRepository#findGroupFriendsByGroupId(Long) groupId로 친구 조회
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/includes/group/{groupId}")
	public List<String> getIncludeGroupFriendNames(@PathVariable("groupId") Long groupId) {
		List<String> ret = groupFriendRepository.findGroupFriendsByGroupId(groupId);
		if (ret == null)
			throw new BadRequestException();
		return ret;
	}

	/**
	 * <pre>
	 *     인자로 받은 해당 그룹에서 인자로 받은 친구들 일괄 삭제
	 *     이미 삭제된 친구가 리스트에 포함되어있을 경우 exception 처리 없이 지나감
	 *     groupId 유니크성으로 요청 멤버에 대한 유효성 검사는 진행하지 않음
	 * </pre>
	 * @param groupId 친구를 삭제하고자 하는 그룹 아이디
	 * @param friendNames 삭제하고자 하는 친구 이름 목록
	 * @return 삭제 성공 반환
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @see openproject.where42.groupFriend.GroupFriendService#deleteIncludeGroupFriends(List, Long)
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/includes/group/{groupId}")
	public ResponseEntity removeIncludeGroupFriends(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.deleteIncludeGroupFriends(friendNames, groupId);
		log.info("[group] {}번 그룹에서 친구가 삭제되었습니다.", groupId);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_FRIENDS_FROM_GROUP), HttpStatus.OK);
	}

	/**
	 * 멤버의 정렬된 전체 친구 이름 목록 조회
	 * @param req 멤버 세션 확인용 HttpServletRequest
	 * @param res 멤버 토큰 확인용 HttpServletResponse
	 * @param key 멤버 토큰 확인용 쿠키값
	 * @return 정렬된 전체 친구 이름 목록 반환
	 * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
	 * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
	 * @see openproject.where42.groupFriend.GroupFriendRepository#findGroupFriendsByGroupId(Long) 멤버의 기본 그룹 아이디로 친구 조회
	 * @since 1.0
	 * @author hyunjcho
	 */
	@GetMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/friendList")
	public List<String> getAllDefaultFriends(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
		String token42 = tokenService.findAccessToken(res, key);
		Member member = memberService.findBySessionWithToken(req, token42);
		return groupFriendRepository.findGroupFriendsByGroupId(member.getDefaultGroupId());
	}

	/**
	 * <pre>
	 *     친구 삭제 api
	 *     기본 그룹을 포함하여 멤버의 모든 커스텀 그룹에서 인자로 받은 친구 일괄 삭제
	 *     이미 삭제된 친구가 리스트에 포함되어있을 경우 exception 처리 없이 지나감
	 * </pre>
	 * @param req 멤버 세션 확인용 HttpServletRequest
	 * @param res 멤버 토큰 확인용 HttpServletResponse
	 * @param key 멤버 토큰 확인용 쿠키값
	 * @param friendNames 삭제하고자 하는 친구 이름 리스트
	 * @return 삭제 성공 반환
	 * @throws BadRequestException 삭제하고자 하는 친구가 존재하지 않는 경우 400 예외 throw
	 * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
	 * @throws openproject.where42.exception.customException.UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
	 * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
	 * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
	 * @see openproject.where42.groupFriend.GroupFriendService#deleteFriends(Member, List) 친구 삭제
	 * @since 1.0
	 * @author hyunjcho
	 */
	@PostMapping(Define.WHERE42_VERSION_PATH + "/groupFriend/friendList")
	public ResponseEntity deleteFriends(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestBody List<String> friendNames) {
		String token42 = tokenService.findAccessToken(res, key);
		Member member = memberService.findBySessionWithToken(req, token42);
		groupFriendService.deleteFriends(member, friendNames);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIENDS), HttpStatus.OK);
	}
}
