package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.DefaultGroupNameException;
import openproject.where42.exception.customException.DuplicateGroupNameException;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Transactional
    public Long createDefaultGroup(Member member, String groupName) { // 이건 밖에서 호출 안되는 건데 굳이 익셉션이 필요하나 싶어
        if (!(groupName.equalsIgnoreCase("기본") || groupName.equalsIgnoreCase("즐겨찾기")))
            throw new DefaultGroupNameException();
        return groupRepository.save(new Groups(groupName, member));
    }

    @Transactional
    public Long createCustomGroup(String groupName, Member owner) {
        validateDuplicateGroupName(owner.getId(), groupName);
        return groupRepository.save(new Groups(groupName, owner));
    }

    public List<Groups> findAllGroupsExceptDefault(Long ownerId) {
        return groupRepository.findGroupsByOwnerId(ownerId);
    }

    public Member findOwnerBySession(HttpServletRequest req, HttpServletResponse res, String key) {
        String token42 = tokenService.getToken(res, key);
        return memberService.findBySessionWithToken(req, token42);
    }

    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);
        validateDuplicateGroupName(group.getOwner().getId(), groupName);
        group.updateGroupName(groupName);
    }

    private void validateDuplicateGroupName(Long ownerId, String groupName) {
        if (groupRepository.isGroupNameInOwner(ownerId, groupName))
            throw new DuplicateGroupNameException();
    }

    @Transactional
    public void deleteByGroupId(Long groupId) {
        groupRepository.deleteByGroupId(groupId);
    }
}