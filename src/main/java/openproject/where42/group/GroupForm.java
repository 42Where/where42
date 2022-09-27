package openproject.where42.group;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.Member;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class GroupForm {
    @NotEmpty(message = "그룹 이름은 필수 입니다.")
    private String groupName;
    private String memberName; // 입력 필드에 없는 것을 프론트가 박아줄 수 있나 임의로?
}
