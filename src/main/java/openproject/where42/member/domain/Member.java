package openproject.where42.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;
import openproject.where42.member.domain.enums.MemberLevel;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends User {
    @Enumerated
    private MemberLevel level;

    private String msg;

    @Enumerated
    private Cluster cluster;

    @Enumerated
    private Floor floor;

    @Enumerated
    private Locate locate;

    public Member(String name, MemberLevel level) {
        this.name = name;
        this.level = level;
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