package openproject.where42.groupFriend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.check.CheckApi;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.MemberRepository;

@Getter
@NoArgsConstructor
public class GroupFriendInfo {

	private Long	id = null; // ID 넣는 로직 추가필요
	private String	name;
	private int		inOutState;
	private String	msg;
	private Locate locate = null;
	private boolean	isMember;
	final private CheckApi checkApi = new CheckApi();

	public GroupFriendInfo setting(String name, MemberRepository memberRepository) {


		Member member = memberRepository.findByName(name);
//		id = ?? 이거 어케 넣어야 하지..?
		this.name = name;
		msg = null;
		inOutState = 0;
		if (member == null){ // 멤버가 아닐 때
			isMember = false;
		} else { // 멤버일 때
			msg = member.getMsg();
			isMember = true;
		}
		check42Hane(name);
		return this;
	}

	private void check42Hane(String name) {
//		if (42hanecall == null){
//			this.inOutState = 0;
//		} else{
			this.inOutState = 1;
			Seoul42 seoul42 = checkApi.check42Api(name);
		if (seoul42.getLocation() != null) {
			locateParse(seoul42);
//		} else if (seoul42.getLocation() == null && isMember == true){
				//수동자리불러오기
			}
//		}
	}
	private void locateParse(Seoul42 seoul42) {
//		Locate tmp = new Locate();
		String seat = seoul42.getLocation();
		System.out.println(seat);
		int i = seat.charAt(1) - '0';

		if (i >= 1 && i <= 6) {
			if (i <= 2)
				this.locate = new Locate(Planet.gaepo, 2, -1, seat);
			else if (i <= 4)
				this.locate = new Locate(Planet.gaepo, 4, -1, seat);
			else
				this.locate = new Locate(Planet.gaepo, 5, -1, seat);
		} else if (i >= 7 && i <= 10)
			this.locate = new Locate(Planet.seocho, -1, i, seat);
		System.out.println(seat.indexOf(1));
	}

}