package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    //회원 가입
    @Transactional // 기본적으로 false여서 안쓰면 false임
    public Long saveGroup(Groups group) {
        validateDuplicateMember(group);
        groupRepository.save(group);
        return group.getId();
    }

    private void validateDuplicateMember(Groups group) {
        String groupName = group.getGroupName();

        if (groupName == "default" || groupName == "starred")
            throw new IllegalStateException("사용할 수 없는 그룹 이름입니다.");
        if (group.getOwner().findGroupName(groupName))
            throw new IllegalStateException("이미 사용하고 있는 그룹 이름입니다.");
    }
}
