package openproject.where42.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.domain.enums.Place;
import openproject.where42.member.domain.enums.MemberLevel;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends User {
    @Enumerated
    private MemberLevel level;

    private String msg;

    private Long defaultGroup;

    private Long starredGroup;

    @Embedded
    private Locate locate;

    public Member(String name, MemberLevel level) {
        this.name = name;
        this.level = level;
    }
    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    public void updateLocate(Planet planet, int floor, int cluster, String spot) {
        this.locate = new Locate(planet, floor, cluster, spot);
    }
}