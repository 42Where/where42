package openproject.where42.group.utils;

import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class GroupUtils {
    private Groups group;

    public List<String> findAllFriends(Groups group) {
        List<GroupMember> groupMembers = group.getGroupMembers();
        List<String> friendsName = new ArrayList<String>();

        if (groupMembers.isEmpty())
            return null; // 배열 비어있으면 알아서 널 반환하려나 예외처리 필요한지?
        for (GroupMember f : groupMembers) {
            friendsName.add(f.getFriendName());
        }
        return friendsName;
    }

    public Boolean haveFriend(Groups group, String friendName) {
        for (GroupMember f : group.getGroupMembers()) {
            if (f.getFriendName() == friendName)
                return true;
        }
        return false;
    }
}
