package openproject.where42.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.enums.Planet;

@Data // data..쓰면 안되려나..
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCadet {
    private String login;
    private String image_url;
    private String location;
    private boolean isFriend; // true 친구, false 아님
    private int inOutState; // -1 정보없음, 0 퇴근, 1 출근
    private String msg;
    private Locate locate;

    public SearchCadet() {}
    public SearchCadet(String login) {
        this.login = login;
    }

    public void updateApiLocate(String seat) { // 42api에서 정보 가져오기
        int i;

        if (seat == null) { // unavailable 어떻게 오는 지 모르겠음
            this.locate = new Locate(null, -1, -1, null);
        } else {
            i = seat.charAt(1) - '0';
            if ((i >= 1 && seat.charAt(2) != '0') && i <= 6) { // api부른 김에 걍 42api 정보는 정리해놓기..? 이렇게 하면 우리가 정보 비워주는 거에도 뭐 딱히 문제는 없을 듯
                if (i <= 2)
                    this.locate = new Locate(Planet.gaepo, 2, 0, seat);
                else if (i <= 4)
                    this.locate = new Locate(Planet.gaepo, 4, 0, seat);
                else
                    this.locate = new Locate(Planet.gaepo, 5, 0, seat);
            } else if (i >= 7 && i <= 9) {
                this.locate = new Locate(Planet.seocho, 0, i, seat);
            } else if (i == 1)
                this.locate = new Locate(Planet.seocho, 0, 10, seat);
        }
    }

    public void updateSelfLocate(Locate locate) {
        this.locate.updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }
}
