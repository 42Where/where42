package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.entity.Groups;

import java.util.List;

@Getter @Setter
public class MemberGroupInfo {
    Long groupId;
    String groupName;
    int count;
    List<String> groupFriends;

    public MemberGroupInfo(Groups g, List<String> groupMembers) {
        this.groupId = g.getId();
        this.groupName = g.getGroupName();
        this.count = groupMembers.size();
        this.groupFriends = groupMembers;
    }
}
