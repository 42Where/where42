package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.FlashMember;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class SearchCadet {
    private String login;
    private Image image;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private boolean isFriend;
    private String location;
    private boolean isParsed;
    private boolean isMember;

    public SearchCadet(Member member) {
        this.login = member.getName();
        this.image = new Image(member.getImg());
        this.msg = member.getMsg();
        this.location = member.getLocation();
        if (this.location == null) {
            this.locate = member.getLocate();
            this.inOrOut = member.getInOrOut();
            this.isParsed = true;
            System.out.println(this.login + " 저는 파스된 멤버 정보예요");
        }
    }

    public SearchCadet(FlashMember flash) {
        this.login = flash.getName();
        this.image = flash.getImage();
        this.location = flash.getLocation();
        if (this.location == null) {
            this.locate = flash.getLocate();
            this.inOrOut = flash.getInOrOut();
            this.isParsed = true;
            System.out.println(this.login + " 저는 파스된 플래시 정보예요");
        }
    }

    public void updateStatus(Locate locate, int inOrOut) {
        this.locate = locate;
        this.inOrOut = inOrOut;
    }

}