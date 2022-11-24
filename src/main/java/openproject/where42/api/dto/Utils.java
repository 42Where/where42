package openproject.where42.api.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.api.ApiService;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.Planet;

@Getter @Setter
public class Utils {

    private static ApiService api = new ApiService();
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;

    // 친구 추가된 애들에 대해서 정보 parse하고 반환, 42 및 멤버일 경우 hane까지 조회 // 이거 고쳐야됨...
    public Utils(String token42, String friendName, Member member) {
        Seoul42 seoul42 = api.get42ShortInfo(token42, friendName);

        this.img = seoul42.getImage().getLink();
        if (member != null) {
            this.msg = member.getMsg();
            Planet planet = api.getHaneInfo(friendName);
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
        } else if (i >= 7 && i <= 9)
            return new Locate(Planet.seocho, 0, i, location);
        return new Locate(Planet.seocho, 0, 10, location);
    }
}
