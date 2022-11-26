package openproject.where42.groupFriend.entity;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Getter @Setter
public class GroupFriendDto {
	private Long id;
	private String name;
	private String img;
	private String msg;
	private Locate locate;
	private int inOrOut;

	public GroupFriendDto(Member friend, Long id) {
		this.id = id;
		this.name = friend.getName();
		this.img = friend.getImg();
		this.msg = friend.getMsg();
		this.locate = friend.getLocate();
		this.inOrOut = friend.getInOrOut();
	}

	public GroupFriendDto(FlashData flash, Long id) {
		this.id = id;
		this.name = flash.getName();
		this.img = flash.getImg();
		this.locate = flash.getLocate();
		this.inOrOut = flash.getInOrOut();
	}
}