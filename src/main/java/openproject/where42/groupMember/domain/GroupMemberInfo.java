package openproject.where42.groupMember.domain;

import lombok.*;
import openproject.where42.groupMember.GroupMemberService;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Getter
@NoArgsConstructor
public class GroupMemberInfo {

	private String	name;
	private String	img;
	private String	inOutState;
	private String	msg;
	private String	seat;
	private String	cluster;
	private String	floor;
	private String	locate;
	private boolean	isMember;
	private int		flag;

	public GroupMemberInfo(Member member) {
		// 이거 그냥 api가 핵심인건가..!?
//		img = 42api_call;
//		inOutState = 42hane_api_call;
//		msg = member에서 getter로 가져옴..?;
//		seat = 42api_call;
//		if (seat == null)
//			seat = 뭐 따로 설정하나;
//		isMember = 1;
//		if (memberRepository.findByName(name) == null)
//			isMember = 0;
	}
}
