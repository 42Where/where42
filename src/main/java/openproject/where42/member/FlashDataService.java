package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Define;
import openproject.where42.api.dto.Image;
import openproject.where42.api.dto.Utils;
import openproject.where42.member.entity.FlashData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FlashDataService {
    private final FlashDataRepository flashDataRepository;
    @Transactional
    public void createFlashData(String name, Image image, String location) {
        FlashData flash = new FlashData(name, image, location);
        flashDataRepository.save(flash);
    }

    public FlashData findByName(String name) {
        return flashDataRepository.findByName(name);
    }

    @Transactional
    public void updateLocation(FlashData flash, String location) {
        flash.updateLocation(location);
    }

    @Transactional
    public void parseStatus(FlashData flash) {
        if (flash.getLocation() != null)
            flash.updateStatus(Utils.parseLocate(flash.getLocation()), Define.IN);
        else
            flash.updateStatus(Utils.parseLocate(flash.getLocation()), Define.NONE);
    }
}
