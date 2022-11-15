package openproject.where42.member.dto;

import lombok.Data;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Utils;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.api.Define;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;

@Data
public class MemberInfo {

    private static final ApiService api = new ApiService();
    private Long id;
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private boolean initFlag;

    public MemberInfo(Member member, String tokenHane, String token42) {
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        Planet planet = api.getHaneInfo(tokenHane, this.name);
        if (planet != null) {
            Seoul42 seoul42 = api.get42ShortInfo(token42, member.getName());
            if (seoul42.getLocation() != null) {
                this.locate = Utils.parseLocate(seoul42.getLocation());
                this.initFlag = true;
            } else {
                this.locate = member.getLocate();
                if (this.locate.getPlanet() == null)
                    this.locate.updateLocate(planet, 0, 0, null);
            }
            this.inOrOut = Define.IN;
        } else {
            this.locate = Utils.parseLocate(null);
            this.initFlag = true;
        }
    }
}
