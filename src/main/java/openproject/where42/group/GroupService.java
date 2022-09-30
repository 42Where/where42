package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.member.repository.MemberRepository;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    //생성 로직
    @Transactional // 기본적으로 false여서 안쓰면 false임
    public Long saveGroup(String groupName) {
        Groups group = new Groups(groupName); // 이렇게 하면 id가 자동으로 들어갈지?
        validateDuplicateGroup(group);
        //groupRepository.save(group);
        return group.getId();
    }

    private void validateDuplicateGroup(Groups group) {
        String groupName = group.getGroupName();

        if (groupName == "default" || groupName == "starred") // default? 기본?
            throw new IllegalStateException("사용할 수 없는 그룹 이름입니다.");
        if (group.getOwner().findGroupName(groupName))
            throw new IllegalStateException("이미 사용하고 있는 그룹 이름입니다.");
    }

    //그룹 이름 수정
    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);

        validateDuplicateGroup(group);
        group.updateGroupName(groupName);
    }
    //삭제 로직
    @Transactional
    public void deleteGroup(Long groupId) {
        groupRepository.deleteGroup(groupId);
    }
}
