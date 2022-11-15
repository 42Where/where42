package openproject.where42.member.dto;

import lombok.Data;
import openproject.where42.group.domain.Groups;

import java.util.List;

@Data
public class MemberGroupInfo {
    Long groupId;
    String groupName;
    List<String> groupFriends;

    public MemberGroupInfo(Groups g, List<String> groupMembers) {
        this.groupId = g.getId();
        this.groupName = g.getGroupName();
        this.groupFriends = groupMembers;
    }
}
