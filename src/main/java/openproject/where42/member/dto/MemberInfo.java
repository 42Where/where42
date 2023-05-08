package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

/**
 * <pre>
 *     멤버 관련 api 반환용 DTO 클래스
 *     id: 멤버 id
 *     name: 멤버 intra Id
 *     img: 멤버 img url
 *     msg: 멤버 상태메시지
 *     locate: 멤버 위치 정보
 *     inOrOut: 멤버 출퇴근 정보
 *     eval: 멤버 동료평가 정보 (ver2에 추가)
 * </pre>
 * @see openproject.where42.member
 * @version 2.0
 * @author hyunjcho
 */
@Getter @Setter
public class MemberInfo {
    private Long id;
    private String name;
    private String img;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private int eval;

    public MemberInfo(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
        this.msg = member.getMsg();
        this.locate = member.getLocate();
        this.inOrOut = member.getInOrOut();
        this.eval = member.getEvaling();
    }
}
