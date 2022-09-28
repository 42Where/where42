package openproject.where42.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.group.domain.GroupMembers;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;
import openproject.where42.member.domain.enums.MemberLevel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends User {
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    List<Groups> groups = new ArrayList<>();

    private String msg;

    @Enumerated
    private MemberLevel level;

    @Enumerated
    private Cluster cluster;

    @Enumerated
    private Floor floor;

    @Enumerated
    private Locate locate;

    public Boolean findGroupName(String groupName) {
        for (Groups g : groups) {
            if (groupName == g.getGroupName()) {
                return true;
            }
        }
        return false;
    }

    public List<String> findNotIncludes(String groupName) { // 해당 그룹에 속해 있지 않은 친구들을 반환하고싶은데..
        List<String> positiveFriends = new ArrayList<>();

        for (Groups g : groups) {
            if (g.getGroupName() == "default") {
                for (GroupMembers f: g.getGroupMembers()) {
                    positiveFriends.add(f.getFriendName());
                }
            }
            if (g.getGroupName() == groupName) {
                for (GroupMembers f: g.getGroupMembers()) {
                    positiveFriends.remove(f.getFriendName());
                }
            }
        }
        return positiveFriends;
    }
}