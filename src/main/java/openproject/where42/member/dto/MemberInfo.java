package openproject.where42.member.dto;

import lombok.Data;
import openproject.where42.api.ApiService;
import openproject.where42.api.Utils;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.api.Define;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;

@Data
public class MemberInfo {

    private static final ApiService api = new ApiService();
    private Long id;
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;

    public MemberInfo (Member member, String token) {
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        System.out.println("memberInfo inout = " + this.inOrOut);
        if (3 == 3) { // hane 출근 검사 부분임 출근 했을 경우.
            Seoul42 seoul42 = api.get42ShortInfo(token, member.getName());
            if (seoul42.getLocation() != null)
                this.locate = Utils.parseLocate(seoul42.getLocation());
            else
                this.locate = member.getLocate();
            this.inOrOut = Define.IN;
        } else {
            this.locate = member.getLocate();
            this.inOrOut = Define.OUT; // 원래 초기화 돼있으면 할 필요 없음.
        }
    }
}
