package openproject.where42.flashData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.Define;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.entity.Locate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 플래시 데이터 관련 서비스 클래스
 * @version 1.0
 * @see openproject.where42.flashData
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FlashDataService {
    private final FlashDataRepository flashDataRepository;
    private final GroupFriendRepository groupFriendRepository;

    /**
     * <pre>
     *     플래시 데이터 생성
     *     멤버 및 플래시 데이터 DB에 없는 카뎃의 아이맥 정보가 백그라운드에서 업데이트 될 경우 생성
     * </pre>
     * @param name 카뎃 인트라 아이디
     * @param img 이미지 주소
     * @param location 아이맥 로그인 정보
     * @return 생성한 플래시 데이터 객체
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public FlashData createFlashData(String name, String img, String location) {
        FlashData flash = new FlashData(name, img, location);
        flashDataRepository.save(flash);
        log.info("[flash-create] \"{}\"님이 flash data에 등록되었습니다.", name);
        return flash;
    }

    /**
     * 이름을 통해 DB 존재 여부 조회
     * @param name 조회할 이름
     * @return 있을 경우 해당 플래시 데이터, 없을 경우 null 반환
     * @since 1.0
     * @author hyunjcho
     */
    public FlashData findByName(String name) {
        return flashDataRepository.findByName(name);
    }

    /**
     * <pre>
     *      백그라운드에서 플래시 데이터의 아이맥 로그인 정보 변경 감지 시 해당 함수를 호출하여 location 정보 갱신
     *      location은 저장만 해둔 상태이며 해당 좌석 정보 파싱이 필요한 시점에 파싱함
     *      [updateTime, location(42api location 정보) 갱신]
     * </pre>
     * @param flash location 정보를 갱신할 플래시 데이터
     * @param location 갱신할 location
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void updateLocation(FlashData flash, String location) {
        log.info("[flash-update] \"{}\"님의 Location이 \"{}\" 에서 \"{}\" (으)로 업데이트 되었습니다.", flash.getName(), flash.getLocation(), location);
        flash.updateLocation(location);
    }

    /**
     * 인자로 넘겨받은 플래시 데이터의 location이 parsed가 아닌 경우 파싱 진행
     * [inOrOut, location(parsed) 갱신]
     * @param flash 파싱 진행할 플래시 데이터
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void parseStatus(FlashData flash) {
        if (Define.PARSED.equalsIgnoreCase(flash.getLocation()))
            return ;
        Locate locate = Locate.parseLocate(flash.getLocation());
        log.info("[flash-setting] \"{}\"님의 Locate가 \"P: {}, F: {}, C: {}, S: {}\" 에서 \"P: {}, F: {}, C: {}, S: {}\" (으)로 바뀌었습니다.", flash.getName()
                ,flash.getLocate().getPlanet(), flash.getLocate().getFloor(), flash.getLocate().getCluster(), flash.getLocate().getSpot()
                ,locate.getPlanet(),locate.getFloor(), locate.getCluster(), locate.getSpot());
        flash.parseStatus(locate);
    }

    // 친구로 등록 된 flashdata 조회, parse가 필요한 경우 parse, 생성해야 하는 경우 생성

    /**
     * <pre>
     *     백그라운드에서 아이맥 로그인 정보가 있는 경우 플래시 데이터에 갱신 되어있으므로 있는 경우 해당 플래시 데이터를 반환,
     *     없는 경우 이미지 db에서 사진 정보 호출 및 location을 null로 만들어 임시 객체를 만들어 정보 반환
     * </pre>
     * @param defaultGroupId 친구 정보를 조회하려는 멤버의 기본 그룹 아이디
     * @param name 조회하고자 하는 친구 인트라 아이디
     * @return 조회하고자 하는 친구의 플래시 데이터
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public FlashData checkFlashFriend(Long defaultGroupId, String name) {
        FlashData flash = findByName(name);
        if (flash != null) {
            if (!Define.PARSED.equalsIgnoreCase(flash.getLocation()))
                flash.parseStatus(Locate.parseLocate(flash.getLocation()));
            return flash;
        }
        flash = new FlashData(name, groupFriendRepository.findImageById(name, defaultGroupId), null);
        flash.parseStatus(Locate.parseLocate(flash.getLocation()));
        return flash;
    }
}