package openproject.where42.member.dto;

import openproject.where42.group.domain.Groups;

import java.util.List;

public class MemberGroupInfo {
    Long groupId;
    String groupName;
    List<String> groupMembers; // 프론트랑 협의 필요

    public MemberGroupInfo(Groups g, List<String> groupMembers) {
        this.groupId = g.getId();
        this.groupName = g.getGroupName();
        this.groupMembers = groupMembers;
    }
}
