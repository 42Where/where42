package openproject.where42.groupFriend.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.groupFriend.domain.GroupFriendInfo;
import openproject.where42.member.domain.Locate;

@Getter
@Setter
public class FriendForm {
	private Long	id = null; // ID 넣는 로직 추가필요
	private String	name;
	private int		inOutState;
	private String	msg;
	private Locate locate;
	private boolean	isMember;

	public FriendForm(Long id, String name, int inOutState, String msg, Locate locate, boolean isMember) {
		this.id = id;
		this.name = name;
		this.inOutState = inOutState;
		this.msg = msg;
		this.locate = locate;
		this.isMember = isMember;
	}

	public FriendForm(GroupFriendInfo groupFriendInfo) {
		this.id = groupFriendInfo.getId();
		this.name = groupFriendInfo.getName();
		this.inOutState = groupFriendInfo.getInOutState();
		this.msg = groupFriendInfo.getMsg();
		this.locate = groupFriendInfo.getLocate();
		this.isMember = groupFriendInfo.isMember();
	}
}
