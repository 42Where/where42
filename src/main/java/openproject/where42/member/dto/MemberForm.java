package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;

@Getter @Setter
public class MemberForm {
    private String msg; // not empty 안 걸어두 되겠징. 글자수 제한은 우리가 두나?
    private Cluster clster;
    private Floor floor;
    private Locate locate;

    public MemberForm(String msg, Cluster cluster, Floor floor, Locate locate) {
        this.msg = msg;
        this.clster = cluster;
        this.floor = floor;
        this.locate = locate;
    }
}