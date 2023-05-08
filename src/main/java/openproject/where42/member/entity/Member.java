package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.util.Define;
import openproject.where42.group.Groups;
import openproject.where42.member.entity.enums.MemberLevel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 여기써줭~~!
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Member extends User {

    @Enumerated
    private MemberLevel level;
    private Long defaultGroupId;
    private Long starredGroupId;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Groups> groups = new ArrayList<Groups>();
    private String img;
    private String msg;
    private int inOrOut;
    @Embedded
    private Locate locate = new Locate(null, 0, 0, null);
    private String location;
    private String signUpDate;
    private Integer evaling;
    @Temporal(TemporalType.TIMESTAMP)
    Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    Date evalDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date updateTime;

    /**
     * 멤버 생성자
     * @param name 멤버 intra Id
     * @param img 멤버 img url
     * @param location 멤버 42api location
     * @param signUpDate 멤버 피신 시작일(ver2에 추가)
     * @param level 멤버 level
     * @since 1.0
     * @author sunghkim
     */
    public Member(String name, String img, String location, String signUpDate, MemberLevel level) {
        this.name = name;
        this.img = img;
        this.location = location;
        this.signUpDate = signUpDate;
        this.level = level;
        this.evaling = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 멤버 생성 시 멤버의 기본 및 즐겨찾기 그룹 아이디 설정
     * @param defaultGroupId 멤버의 기본 그룹 Id
     * @param starredGroupId 멤버의 즐겨찾기 그룹 Id
     * @since 1.0
     * @author sunghkim
     */
    public void setDefaultGroup(Long defaultGroupId, Long starredGroupId) {
        this.defaultGroupId = defaultGroupId;
        this.starredGroupId = starredGroupId;
    }

    /**
     * 멤버의 상태메시지 갱신
     * @param msg 갱신할 상태메시지
     * @since 1.0
     * @author sunghkim
     */
    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 위치 정보 갱신 시각 갱신
     * @since 1.0
     * @author sunghkim
     */
    public void changeTime() {
        this.updateTime = new Date();
    }

    /**
     * 개포/서초/외출/퇴근 플래닛 정보 갱신
     * @param planet 갱신할 planet
     * @since 1.0
     * @author sunghkim
     */
    public void updatePlanet(Planet planet) {
        log.info("[member-update] \"{}\"님의 Planet이 \"{}\"에서 \"{}\"(으)로 업데이트 되었습니다.", this.name, this.getLocate().getPlanet(), planet);
        this.getLocate().updatePlanet(planet);
        this.updateTime = new Date();
    }

    /**
     * location(42api 아이맥 로그인/아웃 정보), 위치 정보 갱신 시각 갱신
     * @param location 갱신할 42api location 정보
     * @since 1.0
     * @author sunghkim
     */
    public void updateLocation(String location) {
        log.info("[member-update] \"{}\"님의 Location이 \"{}\"에서 \"{}\"(으)로 업데이트 되었습니다.", this.name, this.location, location);
        this.location = location;
        this.updateTime = new Date();
    }

    /**
     * 출/퇴근 정보, location(parsed) 갱신
     * @param inOrOut 갱신할 출/퇴근 정보
     * @since 1.0
     * @author sunghkim
     */
    public void updateParsedInOrOut(int inOrOut) {
        this.inOrOut = inOrOut;
        this.location = Define.PARSED;
    }

    /**
     * 출/퇴근 정보 갱신
     * @param inOrOut 갱신할 출/퇴근 정보
     * @since 1.0
     * @author hyunjcho
     */
    public void updateInOrOut(int inOrOut) {
        this.inOrOut = inOrOut;
    }

    /**
     * 출/퇴근 정보, location(parsed), 위치 정보 갱신 시각 갱신
     * @param inOrOut 갱신할 정보
     * @since 1.0
     * @author sunghkim
     */
    public void updateParsedStatus(int inOrOut) {
        this.inOrOut = inOrOut;
        this.location = Define.PARSED;
        this.updateTime = new Date();
    }

    /**
     * <pre>
     *     외출, 혹은 퇴근일 경우 파싱 및 초기화는 진행하지 않고 inOrOut 및 정보 갱신 시각만 갱신
     *     아이맥 로그아웃시 백그라운드에서 정보가 잡히기 떄문에 따로 정보 초기화 진행하지 않음
     *     정보 초기화 시 아이맥 정보 추적이 되지 않는 경우가 발생함
     *     (화면잠금하고 이동할 경우 초기화시 백그라운드에서 로그인 정보를 잡아줄 수 없음)
     * </pre>
     * @param inOrOut 갱신할 정보
     * @since 1.0
     * @author hyunjcho
     */
    public void updateOutStatus(int inOrOut) {
        this.inOrOut = inOrOut;
        this.updateTime = new Date();
    }

    /**
     * 동료평가 상태 및 설정 시각 갱신
     * @param status 갱신할 동료 평가 상태
     * @since 1.0
     * @suthor hyunjcho
    */
    public void updateEval(int status) {
        this.evaling = status;
        this.evalDate = new Date();
    }

    /**
     * 멤버 위치 정보 갱신 시간 계산
     * @return 위치 정보 갱신 시각부터 현재까지의 경과 시간 분으로 반환
     * @since 1.0
     * @author sunghkim
     */
    public Long timeDiff() {
        Date now = new Date();
        return (now.getTime() - updateTime.getTime()) / 60000;
    }

    /**
     * 동료평가 설정 시간 계산
     * @return 동료평가 설정 시각부터 현재까지의 경과 시간 분으로 반환
     * @since 2.0
     * @author sunghkim
     */
    public Long evalTimeDiff() {
        Date now = new Date();
        return (now.getTime() - evalDate.getTime())/ 60000;
    }

    /**
     * 피신 시작일 DB 저장
     * @param date 피신 시작 일
     * @since 2.0
     * @author sunghkim
     */
    public void updateSignUpDate(String date) {
        this.signUpDate = date;
    }

}