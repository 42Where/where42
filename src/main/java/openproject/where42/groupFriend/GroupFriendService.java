package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupFriendService {
	private final GroupFriendRepository groupFriendRepository;
	private final GroupRepository groupRepository;

	// 기본 그룹 친구 추가
	@Transactional
	public Long saveFriend(String friendName, String img, Long defaultGroupId) {
		Groups group = groupRepository.findById(defaultGroupId);
		GroupFriend groupFriend = new GroupFriend(friendName, img, group);
		return groupFriendRepository.save(groupFriend);
	}

	// 커스텀 그룹 친구 추가
	@Transactional
	public void saveGroupFriend(String friendName, Groups group) {
		GroupFriend groupFriend = new GroupFriend(friendName, group);
		groupFriendRepository.save(groupFriend);
	}

	// 커스텀 그룹 일괄 추가
	@Transactional
	public void addFriendsToGroup(List<String> friendNames, Long groupId) {
		Groups group = groupRepository.findById(groupId);
		for (String friendName : friendNames) {
			saveGroupFriend(friendName, group);
		}
	}

	// 친구 한명에 대해 삭제인데, 사용을 안할지도?
	@Transactional
	public void deleteGroupFriend(Long friendId) {
		groupFriendRepository.deleteGroupFriendByGroupFriendId(friendId);
	}

	// 해당 그룹에 포함된 친구들 중 선택된 친구들 일괄 삭제
	@Transactional
	public void deleteIncludeGroupFriends(Long groupId, List<String> friendNames) {
		groupFriendRepository.deleteGroupFriends(groupId, friendNames);
	}

	// 기본 그룹을 포함한 같은 친구에 대해 정보 일괄 삭제
	@Transactional
	public void deleteFriends(Member member, List<String> friendNames) {
		for (String friendName : friendNames)
			groupFriendRepository.deleteFriendByFriendName(member, friendName);
	}
}