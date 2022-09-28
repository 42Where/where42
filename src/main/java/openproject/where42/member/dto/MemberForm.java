package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {
    private Long memberId;
    private String msg; // not empty 안 걸어두 되겠징. 글자수 제한은 우리가 두나?
}
