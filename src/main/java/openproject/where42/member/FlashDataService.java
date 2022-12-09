package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.groupFriend.entity.GroupFriendDto;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Locate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

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
    public FlashData checkFlashFriend(String name, String token42) { // 나중에 멤버 인포랑 그룹프렌드랑 다 합치자 반환하는 애들은
        FlashData flash = findByName(name);
        if (flash != null) {
            if (!Define.PARSED.equalsIgnoreCase(flash.getLocation()))
                flash.parseStatus(Locate.parseLocate(flash.getLocation()));
            return flash;
        }
        else
            flash = createFlashData(name, "img", null); // 이거 지금 에이피아이 타는거 뺸 뻐전임.
        flash.parseStatus(Locate.parseLocate(flash.getLocation()));
        return flash; // 플래시 말고 그룹프렌드 디티오로 반환해주면 될 거 같음 서치도 동일하게 쓸테니
    }
}