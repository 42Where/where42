package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    //생성 로직
    @Transactional // 기본적으로 false여서 안쓰면 false임
    public Long saveGroup(Long memberId, String groupName) {
        Member owner = memberRepository.findById(memberId);
        validateDuplicateGroupName(owner, groupName);
        Groups group = new Groups(groupName, owner);
        groupRepository.save(group);
        return group.getId();
    }

    private void validateDuplicateGroupName(Member owner, String groupName) {
        if (groupName == "friends" || groupName == "starred") // friends(기본), starred(즐겨찾기) 사용불가
            throw new IllegalStateException("사용할 수 없는 그룹 이름입니다.");
        if (groupRepository.haveGroupName(owner, groupName)) // 그룹 이름 중복 확인
            throw new IllegalStateException("이미 사용하고 있는 그룹 이름입니다.");
    }

    //그룹 이름 수정
    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);

        validateDuplicateGroupName(group.getOwner(), groupName);
        group.updateGroupName(groupName);
    }

    public List<String> findAllNotIncludes(Long groupId) {
        return groupRepository.findAllNotIncludes(groupId);
    }

    //삭제 로직
    @Transactional
    public void deleteGroup(Long groupId) {
        Groups group = groupRepository.findById(groupId);
        groupRepository.deleteGroup(group);
    }


}
