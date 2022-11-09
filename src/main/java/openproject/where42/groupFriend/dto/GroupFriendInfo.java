package openproject.where42.groupFriend.dto;

import lombok.Data;
import openproject.where42.api.Utils;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.member.domain.Locate;

@Data
public class GroupFriendInfo {

	private Long id;
	private String name;
	private String img;
	private String msg;
	private Locate locate;
	private int inOutState;
	private boolean isMember;

	public GroupFriendInfo(GroupFriend friend) {
		Utils parseInfo = new Utils(friend.getFriendName());
		this.id = friend.getId();
		this.name = friend.getFriendName();
		this.img = parseInfo.getImg();
		this.msg = parseInfo.getMsg();
		this.locate = parseInfo.getLocate();
		this.inOutState = parseInfo.getInOutState();
		this.isMember = parseInfo.isMember(); // 굳이 필요한지??
	}
}