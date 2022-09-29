package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.GroupMember;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupMemberRepository;
import openproject.where42.group.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupMemberService {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupRepository groupRepository;

	@Transactional
	public Long saveGroupMember(String friend_name, Long groupId) {
		// 엔티티 조회
		Groups group = groupRepository.findById(groupId);

		validateDuplicateGroup(group, friend_name);
//		GroupMember groupMember = new GroupMember(group, friend_name);
//		groupMemberRepository.save(groupMember);
//		return groupMember.getId();
	}

	private void validateDuplicateGroup(Groups group, String friend_name) {
		for (GroupMember gm: group.getGroupMembers()){
			if (gm.getFriendName() == friend_name)
				throw new IllegalStateException("이미 등록된 친구입니다.");
		}
	}

	public void deleteGroupMember(Long groupMemberId) {
//		groupMemberRepository.delete(groupMemberId);
	}

	public void deleteGroupMembers(ArrayList<GroupMember> groupMembers) {
//		for(GroupMember g: groupMembers)
//			groupMemberRepository.delete(g.getId());
	}
}
