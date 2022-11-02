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
    private int inOutStates; // -1 정보없음, 0 퇴근, 1 출근
    private String inOutState;
    private String msg;
    private Locate locate;

    public SearchCadet() {}
    public SearchCadet(String login) {
        this.login = login;
    }

    public void updateApiLocate(String seat) {
        int i;

        if (seat == null) {
            this.locate = new Locate(null, 0, 0, null);
        } else {
            i = seat.charAt(1) - '0';
            if ((i >= 1 && seat.charAt(2) != '0') && i <= 6) {
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
