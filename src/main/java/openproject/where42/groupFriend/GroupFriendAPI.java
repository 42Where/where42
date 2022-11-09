package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupFriendAPI {

	private final GroupFriendService groupFriendService;
	private final GroupFriendRepository groupFriendRepository;

	private final MemberRepository memberRepository;

	@GetMapping("/v1/groupFriend/list/{groupId}")
	public List<String> friendList(@PathVariable("groupId") Long groupId) {
		return groupFriendService.findAllGroupFriendNameByGroupId(groupId);
	}


	/* 이 밑의 함수들 부분은 의도한대로 제대로 반환이 안되고 있어서 쪼매만 더 기다려 주십셔..! */
	/********************************************************************/

	@GetMapping("/v1/groupFriendInfo/{groupId}")
	public List<FriendForm> GroupFriendInfo(@PathVariable("groupId") Long groupId) {
		List<FriendForm> groupFriends = groupFriendService.findAllFriendsInfo(groupId);
		return groupFriends;
	}

	// 해당 친구가 포함되지 않은 그룹 목록 front 반환 --> 멤버가 아닌 친구들은 뭘로 구분..?
	@GetMapping("/v1/groupFriend/notIncludeGroup/{memberId}")
	public List<String> notIncludeGroupByFriend(@PathVariable("memberId") Long memberId, String friendName) {
		return groupFriendRepository.notIncludeGroupByMemberAndFriendName(memberRepository.findById(memberId), friendName);
	}

	// 해당 그룹에 포함되지 않는 친구 목록 front 반환 ---> API
	@GetMapping("/v1/groupFriend/notIncludeFriend/{memberId}")
	public List<String> notIncludeFriendByGroup(@PathVariable("memberId") Long memberId, Long groupId) {
		return groupFriendRepository.notIncludeFriendByGroup(memberRepository.findById(memberId), groupId);
	}
}
