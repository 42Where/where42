package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.domain.enums.Place;

@Getter @Setter
public class MemberForm {
    private String msg; // not empty 안 걸어두 되겠징. 글자수 제한은 우리가 두나?
    private Planet planet;
    private int floor;
    private int cluster;
    private Place place;

    public MemberForm(String msg, Planet planet, int floor, int cluster, Place place) {
        this.msg = msg;
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.place = place;
    }
}