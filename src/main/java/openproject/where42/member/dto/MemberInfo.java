package openproject.where42.member.dto;

import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;

public class MemberInfo {

    private String name;
    private Long id;
    private String msg;
    private String img;
    private int inOutState;

    private Locate locate; //embeded된거 내가 써도 되나?

    //내 상태 조회 메소드
    public MemberInfo getMyInfo(Member member) {
        int i;
        String seat;

        this.name = member.getName();
        this.id = member.getId();
        this.msg = member.getMsg();
//        42api 호출 {
//            this.img = "api 정보";
//            if (seat != null) {// 42자리 정보 있으면, 오류 있을 경우를 제외하고 42api 이후 굳이 하네 안거쳐도 되니까
//                this.inOutState = 1;
//                i = seat.indexOf(1) - '0';
//                if (i >= 1 && i <= 6)
//                    this.locate = new Locate(Planet.gaepo, i, -1, seat);
//                return this;
//            }
//        }
//        if (hane is 출근)
//            return getMyInfo(member);
        return getMyOutInfo(member);
    }

    private MemberInfo getMySelfInfo(Member member) {
        this.inOutState = 1;
        this.locate = member.getLocate();
        return this;
    }

    private MemberInfo getMyOutInfo(Member member) {
        this.inOutState = 0;
        // 외출 관련 로직 추가 여부에 따라 시간 계산 로직 필요
        member.updateLocate(null, -1, -1, null);
        this.locate = member.getLocate();
        return this;
    }
}
