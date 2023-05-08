package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.BadRequestException;
import openproject.where42.group.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 친구 관련 서비스 클래스
 * @version 1.0
 * @see openproject.where42.groupFriend
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupFriendService {
	private final GroupFriendRepository groupFriendRepository;
	private final GroupRepository groupRepository;

	/**
	 * 멤버의 기본 그룹에 인자로 받은 친구 추가
	 * @param friendName 추가하고자 하는 친구의 이름
	 * @param img 추가하고자 하는 친구의 이미지 주소
	 * @param defaultGroupId 멤버의 기본 그룹 id
	 * @return 생성된 친구 아이디 반환
	 * @see openproject.where42.groupFriend.GroupFriendRepository#save(GroupFriend) 친구 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Transactional
	public Long saveFriend(String friendName, String img, String signUpDate, Long defaultGroupId) {
		Groups group = groupRepository.findById(defaultGroupId);
		GroupFriend groupFriend = new GroupFriend(friendName, img, signUpDate, group);
		return groupFriendRepository.save(groupFriend);
	}

	/**
	 * <pre>
	 * 		인자로 받은 커스텀 그룹에 인자로 받은 친구 추가
	 * 		커스텀 그룹의 경우 친구의 이미지 주소를 따로 저장하지 않음
	 * </pre>
	 * @param friendName 추가하고자 하는 친구의 이름
	 * @param group 추가하고자 하는 커스텀 그룹 객체
	 * @see openproject.where42.groupFriend.GroupFriendRepository#save(GroupFriend) 친구 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Transactional
	public void saveGroupFriend(String friendName, Groups group) {
		GroupFriend groupFriend = new GroupFriend(friendName, group);
		groupFriendRepository.save(groupFriend);
	}

	/**
	 * 인자로 받은 커스텀 그룹에 인자로 받은 친구 리스트 추가
	 * @param friendNames 추가하고자 하는 친구 이름 목록
	 * @param groupId 친구를 추가하고자 하는 그룹 아이디
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @see #saveGroupFriend(String, Groups) 친구 저장
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Transactional
	public void addFriendsToGroup(List<String> friendNames, Long groupId) {
		Groups group = groupRepository.findById(groupId);
		if (group == null)
			throw new BadRequestException();
		for (String friendName : friendNames)
			saveGroupFriend(friendName, group);
	}

	/**
	 * 인자로 받은 그룹에서 인자로 받은 친구들 일괄 삭제
	 * @param friendNames 삭제하고 하는 친구 목록
	 * @param groupId 친구를 삭제하고자 하는 그룹 아이디
	 * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
	 * @see openproject.where42.groupFriend.GroupFriendRepository#deleteGroupFriends(Long, List) 친구 삭제
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Transactional
	public void deleteIncludeGroupFriends(List<String> friendNames, Long groupId) {
		if (!groupFriendRepository.deleteGroupFriends(groupId, friendNames))
			throw new BadRequestException();
	}

	/**
	 * <pre>
	 *     인자로 받은 멤버의 친구 중 인자로 받은 친구 일괄 삭제
	 *     모든 커스텀 그룹에서도 삭제됨
	 * </pre>
	 * @param member 삭제 요청 멤버
	 * @param friendNames 삭제하고자 하는 친구 이름 목록
	 * @throws BadRequestException 존재하지 않는 그룹 혹은 없는 친구일 경우 400 예외 throw
	 * @see openproject.where42.groupFriend.GroupFriendRepository#deleteFriendByFriendName(Member, String) 친구 삭제
	 * @since 1.0
	 * @author hyunjcho
	 */
	@Transactional
	public void deleteFriends(Member member, List<String> friendNames) {
		for (String friendName : friendNames)
			groupFriendRepository.deleteFriendByFriendName(member, friendName);
	}
}