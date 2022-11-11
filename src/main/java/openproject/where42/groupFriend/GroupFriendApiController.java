package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupRepository;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.MemberRepository;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupFriendApiController {

	private final GroupFriendService groupFriendService;
	private final GroupFriendRepository groupFriendRepository;
	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;


	// 검색을 통한 친구 등록, 기본 그룹에 등록
	@PostMapping("/v1/groupFriend/{memberId}") // 세션 사용시 v1/groupFriend로 주소 변경
	public ResponseEntity createFriend(@PathVariable("memberId") Long memberId, @RequestParam String friendName) {
		groupFriendService.saveFriend(friendName, memberId);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP_FRIEND), HttpStatus.OK);
	}

	// 해당 그룹에 포함되지 않는 친구 이름 목록 전체 반환
	@GetMapping("/v1/groupFriend/{groupId}/notIncludes/{memberId}")
	public List<String> getNotIncludeGroupFriendNames(@PathVariable("memberId") Long memberId, @PathVariable("groupId") Long groupId) {
		return groupFriendRepository.notIncludeFriendByGroup(memberRepository.findById(memberId), groupId); // repo 함수 이름도 통일 할까?
	}

	// 해당 그룹에 포함되지 않은 친구들 중 선택된 친구들 일괄 추가
	@PostMapping("/v1/groupFriend/{groupId}")
	public ResponseEntity addFriendsToGroup(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.addFriendsToGroup(friendNames, groupId);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.ADD_FRIENDS_TO_GROUP), HttpStatus.OK);
	}

	// 해당 그룹에 포함된 친구 이름 목록 전체 반환
	@GetMapping("/v1/groupFriend/{groupId}/includes")
	public List<String> getIncludeGroupFriendNames(@PathVariable("groupId") Long groupId) {
		return groupFriendRepository.findGroupFriendsByGroupId(groupId);
	}

	// 해당 그룹에 포함된 친구들 중 선택된 친구들 일괄 삭제
	@DeleteMapping("/v1/groupFriend/{groupId}/includes")
	public ResponseEntity removeIncludeGroupFriends(@PathVariable("groupId") Long groupId, @RequestBody List<String> friendNames) {
		groupFriendService.deleteIncludeGroupFriends(groupId, friendNames);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_FRIENDS_FROM_GROUP), HttpStatus.OK);
	}

	// 해당 친구가 포함되지 않은 그룹 목록 front 반환, 친구 선택해서 그룹 추가 하게 하는 거, 프론트 아직 구현 안함
	@GetMapping("/v1/groupFriend/notIncludeGroup/{memberId}") // uri 구조가 좀 이상하긴 하다.. member/{memberId}/groupFriend/~식이 젤 안정적이긴 한데..
	public List<String> notIncludeGroupByFriend(@PathVariable("memberId") Long memberId, @RequestParam String friendName) {
		return groupFriendRepository.notIncludeGroupByMemberAndFriendName(memberRepository.findById(memberId), friendName);
	}

	// 해당 친구에 대해 여러 그룹에 추가, 프론트 아직 구현 안함
	@PostMapping("/v1/groupFriend/groups/{memberId}")
	public ResponseEntity addGroupsToFriends(@PathVariable("memberId") Long memberId, @RequestParam String friendName, @RequestBody List<String> groupNames) {
		for (String groupName : groupNames) {
			Groups g = groupRepository.findGroupsByOwnerIdAndGroupNames(memberId, groupName);
			groupFriendService.saveGroupFriend(friendName, g);
		}
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.ADD_GROUPS_TO_FRIEND), HttpStatus.OK);
	}

	// 해당 친구가 포함된 그룹 전체 삭제, 아직 안 만듦

	// 기본 그룹 친구 이름 목록 반환
	@GetMapping("v1/groupFriend/{memberId}")
	public List<String> getAllDefaultFriends(@PathVariable("memberId") Long memberId) {
		return groupFriendRepository.findGroupFriendsByGroupId(memberRepository.findById(memberId).getDefaultGroupId());
	}

	// 기본 그룹을 포함한 모든 그룹에서 삭제
	@DeleteMapping("/v1/groupFriend/{memberId}") // 프론트 기본에서 삭제하는 경우와 사용자정의 그룹에서만 삭제하는 경우 필히 구분지어서 매핑 필
	public ResponseEntity deleteFriends(@PathVariable("memberId") Long memberId, @RequestBody List<String> friendNames) {
		for (String friendName : friendNames)
			groupFriendService.deleteFriend(memberId, friendName);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIENDS), HttpStatus.OK);
	}
}
