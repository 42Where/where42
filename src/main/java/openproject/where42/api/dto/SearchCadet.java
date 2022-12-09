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

import java.util.ArrayList;

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

    public SearchCadet(Member member) {
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        this.location = member.getLocation();
        this.isMember = true;
        if (Define.PARSED.equalsIgnoreCase(this.location)) {
            this.locate = member.getLocate();
            this.inOrOut = member.getInOrOut();
        }
    }

    public SearchCadet(FlashData flash) {
        this.name = flash.getName();
        this.img = flash.getImg();
        this.location = flash.getLocation();
        if (Define.PARSED.equalsIgnoreCase(this.location)) {
            this.locate = flash.getLocate();
            this.inOrOut = flash.getInOrOut();
        }
    }

    public SearchCadet(String name, String img) {
        this.name = name;
        this.img = img; //웨얼이 주소로 변경 필요
        this.location = Define.PARSED;
        this.locate = new Locate(null, 0, 0, null);
        this.inOrOut = Define.NONE;
    }

    public SearchCadet(String name, String imgUrl, String msg, String spot) {
        this.name = name;
        this.img = imgUrl; // 이미지 주소
        this.msg = msg;
        this.locate = new Locate(null, 0, 0, spot);
        this.inOrOut = Define.IN;
        this.location = Define.PARSED;
        this.isMember = true;
        this.isFriend = true;
    }

    public static ArrayList<SearchCadet> where42() {
        ArrayList<SearchCadet> where42s = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            where42s.add(new SearchCadet(Define.WHERE42NAME.get(i), Define.WHERE42IMG.get(i), Define.WHERE42MSG.get(i), Define.WHERE42SPOT.get(i)));
        return where42s;
    }

    public void updateStatus(Locate locate, int inOrOut) {
        this.locate = locate;
        this.inOrOut = inOrOut;
    }
}