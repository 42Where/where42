package openproject.where42.member.domain;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.domain.GroupMember;
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
public class Member extends User {
    @OneToMany(mappedBy = "owner")
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

    // 이미 해당 멤버가 그 그룹 이름을 가지고 있는지 확인하는 메서드
    public Boolean findGroupName(String groupName) {
        for (Groups g : groups) {
            if (groupName == g.getGroupName())
                return true;
        }
        return false;
    }

    public List<String> findNotIncludes(String groupName) { // 해당 그룹에 속해 있지 않은 친구들을 반환하고싶은데..
        List<String> positiveFriends = new ArrayList<>();

        for (Groups g : groups) {
            if (g.getGroupName() == "default") {
                for (GroupMember f: g.getGroupMembers())
                    positiveFriends.add(f.getFriendName());
            }
            if (g.getGroupName() == groupName) {
                for (GroupMember f: g.getGroupMembers())
                    positiveFriends.remove(f.getFriendName());
            }
        }
        return positiveFriends;
    }

    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    public void updateLocate(Cluster cluster, Floor floor, Locate locate) {
        this.cluster = cluster;
        this.floor = floor;
        this.locate = locate;
    }
}