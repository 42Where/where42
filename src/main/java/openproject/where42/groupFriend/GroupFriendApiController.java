package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupRepository;
import openproject.where42.member.MemberService;
import openproject.where42.member.domain.Member;
import openproject.where42.response.Response;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupFriendApiController {

	private final GroupFriendService groupFriendService;
	private final GroupFriendRepository groupFriendRepository;
	private final MemberService memberService;
	private final GroupRepository groupRepository;


	// 검색을 통한 친구 등록, 기본 그룹에 등록
	@PostMapping("/v1/groupFriend")
	public ResponseEntity createFriend(HttpServletRequest req, @RequestParam String friendName) {
		Member member = memberService.findBySession(req);
		groupFriendService.saveFriend(friendName, member.getDefaultGroupId());
		return new ResponseEntity(Response.res(StatusCode.CREATED, ResponseMsg.CREATE_GROUP_FRIEND), HttpStatus.CREATED);
	}

	// 해당 그룹에 포함되지 않는 친구 이름 목록 전체 반환
	@GetMapping("/v1/groupFriend/notIncludes/group/{groupId}")
	public List<String> getNotIncludeGroupFriendNames(HttpServletRequest req, @PathVariable("groupId") Long groupId) {
		return groupFriendRepository.notIncludeFriendByGroup(memberService.findBySession(req), groupId); // repo 함수 이름도 통일 할까?
	}

	// 해당 그룹에 포함되지 않은 친구들 중 선택된 친구들 일괄 추가, 세션 검사 안함. 저장은 됨
	@PostMapping("/v1/groupFriend/notIncludes/group/{groupId}")
	public ResponseEntity addFriendsToGroup(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.addFriendsToGroup(friendNames, groupId);
		return new ResponseEntity(Response.res(StatusCode.CREATED, ResponseMsg.ADD_FRIENDS_TO_GROUP), HttpStatus.CREATED);
	}

	// 해당 그룹에 포함된 친구 이름 목록 전체 반환, 세션 검사 안함
	@GetMapping("/v1/groupFriend/includes/group/{groupId}")
	public List<String> getIncludeGroupFriendNames(@PathVariable("groupId") Long groupId) {
		return groupFriendRepository.findGroupFriendsByGroupId(groupId);
	}

	// 해당 그룹에 포함된 친구들 중 선택된 친구들 일괄 삭제
	@DeleteMapping("/v1/groupFriend/includes/group/{groupId}")
	public ResponseEntity removeIncludeGroupFriends(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.deleteIncludeGroupFriends(groupId, friendNames);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_FRIENDS_FROM_GROUP), HttpStatus.OK);
	}

	// 해당 친구가 포함되지 않은 그룹 목록 front 반환, 친구 선택해서 그룹 추가 하게 하는 거, 프론트 아직 구현 안함
	@GetMapping("/v1/groupFriend/notIncludeGroup")
	public List<String> notIncludeGroupByFriend(HttpSession session, @RequestParam String friendName) {
		return groupFriendRepository.notIncludeGroupByMemberAndFriendName(memberService.findBySession(session), friendName);
	}

	// 해당 친구에 대해 여러 그룹에 추가, 프론트 아직 구현 안함
//	@PostMapping("/v1/groupFriend/groups")
//	public ResponseEntity addGroupsToFriends(HttpSession session, @RequestParam String friendName, @RequestBody List<String> groupNames) {
//		for (String groupName : groupNames) {
//			Groups g = groupRepository.findGroupsByOwnerIdAndGroupNames(memberId, groupName); // 이 함수 멤버로 구하는 거 고려
//			groupFriendService.saveGroupFriend(friendName, g);
//		}
//		return new ResponseEntity(ResponseDto.res(StatusCode.CREATED, ResponseMsg.ADD_GROUPS_TO_FRIEND), HttpStatus.CREATED);
//	}

	// 해당 친구가 포함된 그룹 전체 삭제, 아직 안 만듦

	// 기본 그룹 친구 이름 목록 반환
	@GetMapping("v1/groupFriend")
	public List<String> getAllDefaultFriends(HttpServletRequest req) {
		return groupFriendRepository.findGroupFriendsByGroupId(memberService.findBySession(req).getDefaultGroupId());
	}

	// 기본 그룹을 포함한 모든 그룹에서 삭제
	@DeleteMapping("/v1/groupFriend") // 프론트 기본에서 삭제하는 경우와 사용자정의 그룹에서만 삭제하는 경우 필히 구분지어서 매핑 필
	public ResponseEntity deleteFriends(HttpServletRequest req, @RequestBody List<String> friendNames) {
		Member member = memberService.findBySession(req);
		groupFriendService.deleteFriends(member, friendNames);
		return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIENDS), HttpStatus.OK);
	}
}
