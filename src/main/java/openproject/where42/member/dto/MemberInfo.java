package openproject.where42.member.dto;

import openproject.where42.check.CheckApi;
import openproject.where42.member.Seoul42;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;

public class MemberInfo {

    private String name;
    private Long id;
    private String msg;
    private String img;
    private int inOutState;

    private CheckApi checkApi = new CheckApi();

    private Locate locate; //embeded된거 내가 써도 되나?

    //내 상태 조회 메소드
    public MemberInfo (Member member) {
        String seat;

        this.name = member.getName();
        this.id = member.getId();
        this.msg = member.getMsg();
        Seoul42 seoul42 = checkApi.check42Api(name);
        if (seoul42.getLocation() != null)
            getAutoInfo(seoul42);
//        42api 호출
//        this.img = "api 정보";
//        if (hane is 출근) {
//            this.inOutState = 1;
//            if (seat != null)
//                getMyAutoInfo(member, seat);
//            else
//                getMySelfInfo(member);
//        } else
//            getMyOutInfo(member);
    }

    private void getAutoInfo(Seoul42 seoul42) {
//		Locate tmp = new Locate();
        String seat = seoul42.getLocation();
        int i = seat.indexOf(1) - '0';

        if (i >= 1 && i <= 6) {
            if (i <= 2)
                this.locate = new Locate(Planet.gaepo, 2, -1, seat);
            else if (i <= 4)
                this.locate = new Locate(Planet.gaepo, 4, -1, seat);
            else
                this.locate = new Locate(Planet.gaepo, 5, -1, seat);
        } else if (i >= 7 && i <= 10)
            this.locate = new Locate(Planet.seocho, -1, i, seat);
    }

    private void getMySelfInfo(Member member) {
        this.locate = member.getLocate();
    }

    private void getMyOutInfo(Member member) {
        this.inOutState = 0;
        // 외출 관련 로직 추가 여부에 따라 시간 계산 로직 필요
        member.getLocate().updateLocate(null, -1, -1, null);
        this.locate = member.getLocate();
    }
}
