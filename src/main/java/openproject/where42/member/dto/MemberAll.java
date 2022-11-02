package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.domain.GroupFriend;

import java.util.List;

@Getter @Setter
public class MemberAll {
    private String name;
    private List<Groups> groupList;
    private List<String> groupFriendList;

    public MemberAll(String name, List<Groups> groupList, List<String> groupFriendList) {
        this.name = name;
        this.groupList = groupList;
        this.groupFriendList = groupFriendList;
    }
}
