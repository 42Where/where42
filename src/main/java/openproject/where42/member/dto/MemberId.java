package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.Member;

@Getter @Setter
public class MemberId {
    Long id;
    String name;

    public MemberId(Member member) {
        this.id = member.getId();
        this.name = member.getName();
    }
}
