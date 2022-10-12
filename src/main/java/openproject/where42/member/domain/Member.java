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

    @Enumerated
    private Planet planet;

    private int floor;

    private int cluster;

    @Enumerated
    private Place place;

    public Member(String name, MemberLevel level) {
        this.name = name;
        this.level = level;
    }
    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    public void updateLocate(Planet planet, int floor, int cluster, Place place) {
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.place = place;
    }
}