package openproject.where42.search;

import lombok.Data;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.enums.Planet;

import java.util.List;

@Data // data..쓰면 안되려나..
public class Seoul42 {
    private String name;
    private String img;
    private boolean isFriend; // true 친구, false 아님
    private int inOutState; // -1 정보없음, 0 퇴근, 1 출근
    private String msg;

    private Locate locate;
//    private Planet planet; // -1 정보 없음, 0 개포, 1 서초
//    private int floor; // -1 정보 없음, 0 지하 1층, 1 ~ 5층, 6 옥상
//    private int cluster; // -1 정보 없음, 7 ~ 10 클
//    private String spot;

    public Seoul42(String name, String img, String seat) { // 42api에서 정보 가져오기
        this.name = name;
        this.img = img;
        this.isFriend = false; // 이거 유저 선택 안해도 이 사람이 친구인지 아닌지 나오게끔 하기로 했던 것 같은데 그러면 42api 검색 시에 우리 db 뒤져야 함. 아니라면 프론트에 의견 전달해서 디자인 수정 부탁해야함 의견 필.
        this.inOutState = -1; //
        this.msg = null;
        int i;
        if (seat == null) { // unavailable 어떻게 오는 지 모르겠음
            this.inOutState = 0;
            this.locate = new Locate(null, -1, -1, null);
        }
        else {
            i = seat.indexOf(1) - '0';
            if (i >= 1 && i <= 6) { // api부른 김에 걍 42api 정보는 정리해놓기..? 이렇게 하면 우리가 정보 비워주는 거에도 뭐 딱히 문제는 없을 듯
                if (i <= 2)
                    this.locate = new Locate(Planet.gaepo, 2, -1, seat);
                else if (i <= 4)
                    this.locate = new Locate(Planet.gaepo, 4, -1, seat);
                else
                    this.locate = new Locate(Planet.gaepo, 5, -1, seat);
            }
            else if (i >= 7 && i <= 9 ) {
                this.locate = new Locate(Planet.seocho, -1, i, seat);
        }
    }

//    public static List<Seoul42> search42seoul(String searchWord) {
//        List<String> matchAllCadet = null; // "api한테 일단 유저 리스트 받아오기
//        for (String cadet : matchAllCadet) {
//            api.(cadet); // string 마다 정보 또 호출해가지고 하나씩 정보 가져와
//            matchAllCadet.add(new Seoul42(apiName, apiImg, apiSeat));
//        }
//        return matchAllCadet;
    }
}
