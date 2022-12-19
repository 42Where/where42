package openproject.where42.flashData;

import lombok.RequiredArgsConstructor;
import openproject.where42.util.Define;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.entity.Locate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FlashDataService {
    private final FlashDataRepository flashDataRepository;
    private final GroupFriendRepository groupFriendRepository;

    @Transactional
    public FlashData createFlashData(String name, String img, String location) {
        FlashData flash = new FlashData(name, img, location);
        flashDataRepository.save(flash);
        return flash;
    }

    public FlashData findByName(String name) {
        return flashDataRepository.findByName(name);
    }

    // 검색 시 [location(api set), updateTime 갱신], parse x
    @Transactional
    public void updateLocation(FlashData flash, String location) {
        flash.updateLocation(location);
    }

    // 검색 만 하고 선택되거나 친구로 조회되지 않은 경우, parse 및 update
    @Transactional
    public void parseStatus(FlashData flash) {
        flash.parseStatus(Locate.parseLocate(flash.getLocation()));
    }

    // 친구로 등록 된 flashdata 조회, parse가 필요한 경우 parse, 생성해야 하는 경우 생성
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