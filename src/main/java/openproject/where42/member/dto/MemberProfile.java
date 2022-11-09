package openproject.where42.member.dto;

import lombok.Getter;
import openproject.where42.member.domain.Locate;

@Getter
public class MemberProfile {
    private String msg;
    private Locate locate;
    public MemberProfile(String msg, Locate locate) {
        this.msg = msg;
        this.locate = locate;
    }
}