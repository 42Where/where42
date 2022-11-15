package openproject.where42.api.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;
@Data
@RequiredArgsConstructor
public class Utils { // api에 넣어도 괜찮을듯..?

    private static ApiService api = new ApiService(); // 어차피 계속 부를거 static으로 해놓는 게 좋을지두
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;

    // 친구 추가된 애들에 대해서 정보 parse하고 반환, 42 및 멤버일 경우 hane까지 조회
    public Utils(String token42, String tokenHane, String friendName, Member member) {
        Seoul42 seoul42 = api.get42ShortInfo(token42, friendName);

        this.img = seoul42.getImage_url();
        if (member != null) {
            this.msg = member.getMsg();
            Planet planet = api.getHaneInfo(tokenHane, friendName);
            if (planet != null) {
                if (seoul42.getLocation() != null)
                    this.locate = parseLocate(seoul42.getLocation());
                else {
                    this.locate = member.getLocate();
                    if (this.locate.getPlanet() == null)
                        this.locate.updateLocate(planet, 0, 0, null);
                }
                this.inOrOut = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
            }
        } else {
            if (seoul42.getLocation() != null) {
                this.locate = parseLocate(seoul42.getLocation());
                this.inOrOut = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOrOut = Define.NONE;
            }
        }
    }

    // 검색 후 한명 선택 시 member 여부에 따라 하네, 출근, 자리 정보등 다시 parse 후 반환 (42api 이미 정리된 상태)
    public Utils(String tokenHane, Member member, String location) { // hane token 받아야 함
        if (member != null) {
            this.msg = member.getMsg();
            Planet planet = api.getHaneInfo(tokenHane, member.getName());
            if (planet != null) {// hane 출근 확인 로직
                if (location != null)
                    this.locate = parseLocate(location);
                else {
                    this.locate = member.getLocate();
                    if (this.locate.getPlanet() == null)
                        this.locate.updateLocate(planet, 0, 0, null);
                }
                this.inOrOut = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
            }
        } else {
            if (location != null) {
                this.locate = parseLocate(location);
                this.inOrOut = Define.IN;
            } else {
                this.locate = new Locate(null, 0, 0, null);
                this.inOrOut = Define.NONE;
            }
        }
    }

    public static Locate parseLocate(String location) {
        if (location == null) {
            return new Locate(null, 0, 0, null);
        }
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
