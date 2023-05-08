package openproject.where42.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Administrator;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * 검색 정보 DTO 클래스
 * name: 검색 카뎃 인트라 아이디
 * img: 검색 카뎃 이미지 url
 * msg: 검색 카뎃 상태메시지
 * locate: 검색 카뎃 locate 정보
 * inOrOut: 검색 카뎃 출퇴근 정보
 * isFriend: 검색된 카뎃의 검색한 카뎃 친구 여부
 * eval: 검색 카뎃 동료평가 정보
 * isMember: 검색된 카뎃 멤버 여부
 * @version 1.0
 */
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
    private int eval;
    private boolean isMember;

    /**
     * 멤버인 카뎃 검색시 사용 생성자
     * @param member 검색 카뎃이 멤버인 경우 멤버 정보
     * @since 1.0
     * @author hyunjcho
     */
    public SearchCadet(Member member) {
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        this.eval = member.getEvaling();
        this.isMember = true;
    }

    /**
     * 멤버는 아니지만 플래시 데이터에 존재하는 카뎃 검색시 사용 생성자
     * @param flash 검색 카뎃이 플래시 데이터에 존재하는 경우 플래시 데이터 정보
     */
    public SearchCadet(FlashData flash) {
        this.name = flash.getName();
        this.img = flash.getImg();
    }

    /**
     * <pre>
     *     멤버도 아니고 플래시 데이터에도 존재하지 않는 카뎃 검색시 사용 생성자로 DB에 저장하지 않고 임시 객체만 생성
     *     이 경우 아이맥 로그인 정보는 무조건 Null
     *     이미지가 존재하지 않는 블랙홀 카뎃일 경우 블랙홀 이미지로 대체
     * </pre>
     * @param name 검색 카뎃이 멤버, 플래시 데이터 모두에 존재하지 않는 경우 검색 카뎃 이름
     * @param img 검색 카뎃 이미지 url
     * @since 1.0
     * @author hyunjcho
     */
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

    /**
     * @since 1.0
     * @author hyunjcho
     */
    public SearchCadet(String name, String img, String msg, String spot) {
        this.name = name;
        this.img = img;
        this.msg = msg;
        this.locate = new Locate(null, 0, 0, spot);
        this.inOrOut = Define.IN;
        this.location = Define.PARSED;
        this.isMember = true;
        this.isFriend = true;
    }

    /**
     * 검색 카뎃 정보 파싱 시
     * @param locate 파싱 locate
     * @param inOrOut 파싱 출퇴근 정보
     * @since 1.0
     * @author hyunjcho
     */
    public void updateStatus(Locate locate, int inOrOut) {
        this.locate = locate;
        this.inOrOut = inOrOut;
    }
}