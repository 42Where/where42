package openproject.where42.groupFriend;

import java.util.Comparator;

public class StringComparator implements Comparator<GroupFriend> {
    @Override
    public int compare(GroupFriend first, GroupFriend second) {
        String firstName = first.getFriendName();
        String secondName = second.getFriendName();

        return firstName.compareTo(secondName);
    }
}
