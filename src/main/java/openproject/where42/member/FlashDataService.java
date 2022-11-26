package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Locate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FlashDataService {
    private final FlashDataRepository flashDataRepository;
    private static final ApiService apiservice = new ApiService();

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
    public FlashData checkFlashFriend(String name, String token42) {
        FlashData flash = findByName(name);
        if (flash != null && flash.timeDiff() < 3) {
            if (!Define.PARSED.equalsIgnoreCase(flash.getLocation()))
                flash.parseStatus(Locate.parseLocate(flash.getLocation()));
            return flash;
        }
        Seoul42 seoul42 = apiservice.get42ShortInfo(token42, name);
        if (flash != null)
            flash.updateLocation(seoul42.getLocation());
        else
            flash = createFlashData(name, seoul42.getImage().getLink(), seoul42.getLocation());
        flash.parseStatus(Locate.parseLocate(flash.getLocation()));
        return flash;
    }
}
