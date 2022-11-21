package openproject.where42.groupFriend.entity;

import lombok.Data;
import openproject.where42.api.dto.Utils;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Data
public class GroupFriendDto {

	private Long id;
	private String name;
	private String img;
	private String msg;
	private Locate locate;
	private int inOrOut;

	public GroupFriendDto(String token42, GroupFriend friend, Member member) {
		Utils parseInfo = new Utils(token42, friend.getFriendName(), member);
		this.id = friend.getId();
		this.name = friend.getFriendName();
		this.img = parseInfo.getImg();
		this.msg = parseInfo.getMsg();
		this.locate = parseInfo.getLocate();
		this.inOrOut = parseInfo.getInOrOut();
	}
}