package openproject.where42.groupMember.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class GroupMemberForm {
	@NotEmpty(message = "친구 이름을 작성하세요")
	private String	friend_name;
	private Long	groupId;
}
