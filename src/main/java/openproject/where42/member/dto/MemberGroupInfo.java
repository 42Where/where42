package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.Groups;

import java.util.List;

/**
 * <pre>
 *     멤버가 가진 그룹들에 대한 요약 정보 DTO 클래스
 *     groupId: 그룹 id
 *     groupName: 그룹 이름
 *     count: 해당 그룹에 포함된 친구들의 수
 *     groupFriends: 해당 그룹에 포함된 친구들의 이름 목록
 * </pre>
 * @see openproject.where42.group
 * @version 1.0
 * @author hyunjcho
 */
@Getter @Setter
public class MemberGroupInfo {
    private Long groupId;
    private String groupName;
    private int count;
    private List<String> groupFriends;

    public MemberGroupInfo(Groups g, List<String> groupMembers) {
        this.groupId = g.getId();
        this.groupName = g.getGroupName();
        this.count = groupMembers.size();
        this.groupFriends = groupMembers;
    }
}
