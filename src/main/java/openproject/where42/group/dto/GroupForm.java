package openproject.where42.group.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.Member;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class GroupForm {
    @NotEmpty(message = "그룹 이름은 필수 입니다.")
    private String groupName; // 만약 아래거 필요없으면 변수 1개인데 form이 필요한가?
    private Long memberId; // 이 변수 굳이 필요한가?
}
