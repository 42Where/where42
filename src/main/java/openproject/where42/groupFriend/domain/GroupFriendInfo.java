package openproject.where42.groupFriend.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import openproject.where42.member.MemberService;
import openproject.where42.member.OAuthToken;
import openproject.where42.member.Seoul42;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Getter
public class GroupFriendInfo {

	private Long	id; // ID 넣는 로직 추가필요;
	private String	name;
	private int		inOutState;
	private String	msg;
	private Locate	locate;
	private boolean	isMember;
	private int		flag;
	private final MemberRepository memberRepository;
	private final MemberService memberService;

	public GroupFriendInfo(MemberRepository memberRepository, MemberService memberService) {
		this.memberRepository = memberRepository;
		this.memberService = memberService;
	}

	public void setting(String name, OAuthToken oAuthToken) {
		Member member = memberRepository.findByName(name);
		this.name = name;
		msg = null;
		inOutState = 0;
		if (member == null){ // 멤버가 아닐 때
			isMember = false;
		} else { // 멤버일 때
			msg = member.getMsg();
			isMember = true;
		}
		check42Hane(name, oAuthToken);
	}

	private void check42Hane(String name, OAuthToken oAuthToken) {
		this.locate = new Locate();
//		if (42hanecall == null){
//			this.inOutState = 0;
//		} else{
		this.inOutState = 1;
		check42Api(name, oAuthToken);
//		}
	}

	private void check42Api(String name, OAuthToken oAuthToken) {
		ObjectMapper objectMapper = new ObjectMapper();
		Seoul42 seoul42 = null;

		ResponseEntity<String> response = memberService.callNameInfo(name, oAuthToken);
		try {
			seoul42 = objectMapper.readValue(response.getBody(), Seoul42.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (seoul42.getLocation() == null){
			
		}

	}
}
