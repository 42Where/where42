package openproject.where42.api;

import lombok.Data;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.check.CheckApi;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;
@Data
public class Utils { // api에 넣어도 괜찮을듯..?
    private static CheckApi checkApi = new CheckApi(); // 어차피 계속 부를거 static으로 해놓는 게 좋을지두
    private String img;
    private String msg;
    private Locate locate;
    private int inOutState;
    private boolean isMember;

    public Utils(String name, Member member) {
        Seoul42 seoul42 = checkApi.check42Api(name);

        this.img = seoul42.getImage_url();
        if (member != null) {
            this.msg = member.getMsg();
            if (3 == 3) {// hane 출근 확인 로직
                if (seoul42.getLocation() != null)
                    this.locate = parseLocate(seoul42.getLocation());
                else
                    this.locate = member.getLocate();
                this.inOutState = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOutState = Define.OUT;
            }
            this.isMember = true;
        } else {
            if (seoul42.getLocation() != null) {
                this.locate = parseLocate(seoul42.getLocation());
                this.inOutState = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOutState = Define.NONE;
            }
        }
    }

    public Utils(Member member, String locate) {
        if (member != null) {
            this.msg = member.getMsg();
            if (3 == 3) {// hane 출근 확인 로직
                if (locate != null)
                    this.locate = parseLocate(locate);
                else
                    this.locate = member.getLocate();
                this.inOutState = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOutState = Define.OUT;
            }
            this.isMember = true;
        } else {
            if (locate != null) {
                this.locate = parseLocate(locate);
                this.inOutState = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOutState = Define.NONE;
            }
        }
    }

    public static Locate parseLocate(String location) {
        int i = location.charAt(1) - '0';
        if ((i >= 1 && location.charAt(2) != '0') && i <= 6) {
            if (i <= 2)
                return new Locate(Planet.gaepo, 2, 0, location);
            else if (i <= 4)
                return new Locate(Planet.gaepo, 4, 0, location);
            else
                return new Locate(Planet.gaepo, 5, 0, location);
        } else if (i >= 7 && i <= 9) {
            return new Locate(Planet.seocho, 0, i, location);
        } else if (i == 1)
            return new Locate(Planet.seocho, 0, 10, location);
        return null; // 예외처리 throw 날려야 하나?
    }
}
