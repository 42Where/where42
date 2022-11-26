package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Getter @Setter
public class MemberInfo {
    private Long id;
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;

    public MemberInfo(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        this.locate = member.getLocate();
        this.inOrOut = member.getInOrOut();
    }
}
