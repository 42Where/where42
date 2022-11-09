package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.NotCustomGroupFriend;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupFriend.dto.GroupFriendShortInfo;
import openproject.where42.groupFriend.repository.GroupFriendRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
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

	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;
	private final GroupFriendService groupFriendService;
	private final GroupFriendRepository groupFriendRepository;


	@PostMapping("/v1/member/{memberId}/groupFriends")
	public ResponseEntity createFriend(@PathVariable("memberId") Long memberId, @RequestParam String friendName) {
		Member member = memberRepository.findById(memberId);
		groupFriendService.saveGroupFriend(friendName, member.getDefaultGroupId());
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP_FRIEND), HttpStatus.OK); // 실패하는 경우 예외가 있을까?
	}

	// 해당 친구가 포함되지 않은 그룹 목록 front 반환, SO, 어떤 친구 클릭해서 어떤 그룹에 추가할지 고를 수 있도록
	@GetMapping("/v1/groupFriend/notIncludeGroup/{memberId}") // uri 구조가 좀 이상하긴 하다.. member/{memberId}/groupFriend/~식이 젤 안정적이긴 한데..
	public List<String> notIncludeGroupByFriend(@PathVariable("memberId") Long memberId, @RequestParam String friendName) {
		return groupFriendRepository.notIncludeGroupByMemberAndFriendName(memberRepository.findById(memberId), friendName);
	}

	// 해당 친구에 대해 여러 그룹에 추가
	@PostMapping("/v1/groupFriend/groups/{memberId}")
	public ResponseEntity addGroupsToFriends(@PathVariable("memberId") Long memberId, @RequestParam String friendName, @RequestBody List<String> groupNames) {
		for (String groupName : groupNames) {
			Groups g = groupRepository.findGroupsByOwnerIdAndGroupNames(memberId, groupName);
			groupFriendService.saveGroupFriend(friendName, g.getId()); // 좀 더 효율적인 방법 없는 지?
		}
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.ADD_GROUPS_TO_FRIEND), HttpStatus.OK);
	}

	// 해당 그룹에 포함되지 않는 친구 목록 front 반환, so, 어떤 그룹 클릭해서 어떤 친구들을 추가할지 고를 수 있도록
	@GetMapping("/v1/groupFriend/notIncludeFriend/{memberId}/{groupId}")
	public List<String> notIncludeFriendByGroup(@PathVariable("memberId") Long memberId, @PathVariable("groupId") Long groupId) {
		return groupFriendRepository.notIncludeFriendByGroup(memberRepository.findById(memberId), groupId);
	}

	// 해당 그룹에 대해 여러 친구 추가
	@PostMapping("/v1/groupFriend/Friends/{groupId}")
	public ResponseEntity addFriendsToGroup(@PathVariable("groupId") Long groupId, @RequestBody List<String> friends) {
		Groups group = groupRepository.findById(groupId);
		groupFriendService.addFriendsToGroup(friends, group); // 로직 만들어야 함
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.ADD_FRIENDS_TO_GROUP), HttpStatus.OK);
	}

	// 커스텀 그룹에서 친구를 삭제할 경우 프론트 선택 된 친구들에 대해 그룹이름, Id 같이 넘겨줘야 함
	@DeleteMapping("/groupFriend/customGroup")
	public ResponseEntity deleteGroupFriend(@RequestBody List<GroupFriendShortInfo> friendList) {
		for (GroupFriendShortInfo friend : friendList) {
			if (friend.getGroupName().equalsIgnoreCase("기본"))
				throw new NotCustomGroupFriend();
			else
				groupFriendService.deleteGroupFriend(friend.getId());
		}
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FROM_FRIEND), HttpStatus.OK);
	}

	// 아예 기본 그룹 자체에서 삭제할 경우 (그룹별로 선택해서 친구  다중 삭제 가능하게 할 것인지? -> 이거 디폴트랑 구분돼야 해서 기본그룹에서만 다중 삭제 가능하게 해야할지도. 만약 시킬거라면?)
	@DeleteMapping("/groupFriend/default/{memberId}") // 프론트 기본에서 삭제하는 경우와 사용자정의 그룹에서만 삭제하는 경우 필히 구분지어서 매핑 필
	public ResponseEntity deleteFriend(@PathVariable("memberId") Long memberId, @RequestParam List<String> friendNames) {
		for (String friendName : friendNames)
			groupFriendService.deleteFriend(memberId, friendName);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIEND), HttpStatus.OK);
	}
}
