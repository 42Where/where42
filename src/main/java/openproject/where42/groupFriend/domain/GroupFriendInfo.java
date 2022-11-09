package openproject.where42.groupFriend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.api.Utils;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.check.CheckApi;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.MemberRepository;

@Getter
@NoArgsConstructor
public class GroupFriendInfo {

	private Long id;
	private String name;
	private String img;
	private String msg;
	private Locate locate;
	private int inOutState;
	private boolean isMember;

	public GroupFriendInfo(GroupFriend friend, Member member) {
		Utils parseInfo = new Utils(friend.getFriendName(), member);
		this.id = friend.getId();
		this.name = friend.getFriendName();
		this.img = parseInfo.getImg();
		this.msg = parseInfo.getMsg();
		this.locate = parseInfo.getLocate();
		this.inOutState = parseInfo.getInOutState();
		this.isMember = parseInfo.isMember(); // 굳이 필요한지??
	}
}