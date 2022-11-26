package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.api.Define;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchCadet {
    private String login;
    private Image image;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private boolean isFriend;
    private String location;
    private boolean isMember;

    public SearchCadet(Member member) {
        this.login = member.getName();
        this.image = new Image(member.getImg());
        this.msg = member.getMsg();
        this.location = member.getLocation();
        if (Define.PARSED.equalsIgnoreCase(this.location)) {
            this.locate = member.getLocate();
            this.inOrOut = member.getInOrOut();
        }
    }

    public SearchCadet(FlashData flash) {
        this.login = flash.getName();
        this.image = new Image(flash.getImg());
        this.location = flash.getLocation();
        if (Define.PARSED.equalsIgnoreCase(this.location)) {
            this.locate = flash.getLocate();
            this.inOrOut = flash.getInOrOut();
        }
    }

    public void updateStatus(Locate locate, int inOrOut) {
        this.locate = locate;
        this.inOrOut = inOrOut;
    }
}