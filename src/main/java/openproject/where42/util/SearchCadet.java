package openproject.where42.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchCadet {
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private boolean isFriend;
    private String location;
    private boolean isMember;

    // 멤버용
    public SearchCadet(Member member) {
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        this.isMember = true;
    }

    // 플래시 데이터
    public SearchCadet(FlashData flash) {
        this.name = flash.getName();
        this.img = flash.getImg();
    }

    // 플래시 데이터에도 없는 경우 디비에 저장하지 않고 location null 셋팅
    public SearchCadet(String name, String img) {
        this.name = name;
        if (img != null)
            this.img = img;
        else
            this.img = "img/blackhole.JPG";
        this.locate = new Locate(null, 0, 0, null);
        this.inOrOut = Define.NONE;
        this.location = Define.PARSED;
    }

    // 파싱 시
    public void updateStatus(Locate locate, int inOrOut) {
        this.locate = locate;
        this.inOrOut = inOrOut;
    }
}