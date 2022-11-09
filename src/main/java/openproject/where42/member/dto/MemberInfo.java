package openproject.where42.member.dto;

import lombok.Data;
import openproject.where42.api.Utils;
import openproject.where42.check.CheckApi;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.api.Define;
import openproject.where42.member.MemberService;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;

@Data
public class MemberInfo {

    private MemberService memberService;
    private CheckApi checkApi;
    private Long id;
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOutState;

    public MemberInfo (Member member, int inOutState) { // 맨처음 만들어지면서 로그인 할 때
        this.id = member.getId();
        this.name = member.getName();
//        this.img = member.img(); // 멤버 엔티티 수정되면 살리기
        this.msg = member.getMsg();
        this.locate = member.getLocate();
        this.inOutState = inOutState;
    }

    //내 상태 조회 메소드
    public MemberInfo (Member member) { // 두번쨰부터 로그인 시
        this.id = member.getId();
        this.name = member.getName();
//        this.img = member.getImg(); // 멤버 엔티티 수정되면 살리기
        this.msg = member.getMsg();
        if (3 == 3) { // hane 출근 검사 부분임 출근 했을 경우.
            Seoul42 seoul42 = checkApi.check42Api(name);
            if (seoul42.getLocation() != null) {
                this.locate = Utils.parseLocate(seoul42.getLocation());
                memberService.initializeLocate(member); // 좌석 정보 있으면 수동 정보 날리기
            } else {
                this.locate = member.getLocate();
            }
            this.inOutState = Define.IN;
        } else {
            memberService.initializeLocate(member); // 만약 정보가 있으면 날려줘야 하니까
            this.locate = member.getLocate();
            this.inOutState = Define.OUT; // 원래 초기화 돼있으면 할 필요 없음.
        }
    }


}
