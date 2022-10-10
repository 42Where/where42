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

    @Transactional
    public Long createDefaultGroup(Member member, String groupName) {
        Long l = Long.valueOf(0);

        if (!(groupName == "기본" || groupName == "즐겨찾기")) // 기본이나 즐겨찾기 아닌 다른 그룹 혹시 잘못 호출 시 예외 터트릴 건데 어디서 잡을 지 모르겠어서 일단 걍 리턴 나중에 에러처리 필요
            return l;
        Groups group = new Groups(groupName, member);
        groupRepository.save(group); // 얘 id 반환 안해주나?
        return group.getId();
    }
    @Transactional // 기본적으로 false여서 안쓰면 false임
    public void saveGroup(String groupName, Long ownerId) {
        validateDuplicateGroupName(ownerId, groupName);
        Member owner = memberRepository.findById(ownerId);
        Groups group = new Groups(groupName, owner);
        groupRepository.save(group);
    }

    //그룹 이름 수정
    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);

        validateDuplicateGroupName(group.getOwner().getId(), groupName);
        group.updateGroupName(groupName);
    }

    //중복 검증
    private void validateDuplicateGroupName(Long ownerId, String groupName) { // 여러곳에서 호출 시에 대한 에러 처리 필요 싱글톤 패턴 참고
        if (groupRepository.findByOwnerIdAndName(ownerId, groupName) != null) // boolean.. 새 함수 만들기..? haveGroupName 등 boolean 반환 함수 생기면 교체 이걸 예외로 떤지나?
            throw new IllegalStateException("이미 사용하고 있는 그룹 이름입니다.");
    }

    public List<Groups> findGroups(Long ownerId) {
        return null;
//        return groupRepository.findGroupsByOwnerId(ownerId);
    }

    //삭제 로직
    @Transactional
    public void deleteByGroupId(Long groupId) {
        groupRepository.deleteByGroupId(groupId);
    }
}