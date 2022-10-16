package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.enums.Planet;

@Getter @Setter
public class MemberProfile {
    private String msg; // not empty 안 걸어두 되겠징. 글자수 제한은 우리가 두나?
    private Locate locate;
//    private Planet planet;
//    private int floor;
//    private int cluster;
//    private String spot;

//    public MemberProfile(String msg, Planet planet, int floor, int cluster, String spot) {
//        this.msg = msg;
//        this.planet = planet;
//        this.floor = floor;
//        this.cluster = cluster;
//        this.spot = spot;
//    }
    public MemberProfile(String msg, Locate locate) {
        this.msg = msg;
        this.locate = locate;
    }
}