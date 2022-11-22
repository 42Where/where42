package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.entity.Groups;

import java.util.List;

@Getter @Setter
public class MemberGroupInfo {
    private Long groupId;
    private String groupName;
    private int count;
    private List<String> groupFriends;

    public MemberGroupInfo(Groups g, List<String> groupMembers) {
        this.groupId = g.getId();
        this.groupName = g.getGroupName();
        if (groupMembers == null)
            this.count = 0;
        else
            this.count = groupMembers.size();
        this.groupFriends = groupMembers;
    }
}
