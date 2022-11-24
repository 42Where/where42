package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Define;
import openproject.where42.api.dto.Image;
import openproject.where42.api.dto.Utils;
import openproject.where42.member.entity.FlashMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FlashDataService {
    private final FlashMemberRepository flashMemberRepository;
    @Transactional
    public void createFlashData(String name, Image image, String Location) {
        FlashMember flash = new FlashMember(name, image, location);
        flashMemberRepository.save(flash);
    }

    public FlashMember findByName(String name) {
        return flashMemberRepository.findByName(name);
    }

    @Transactional
    public void parseStatus(FlashMember flash) {
        if (flash.getLocation() != null)
            flash.updateStatus(Utils.parseLocate(flash.getLocation()), Define.IN);
        else
            flash.updateStatus(Utils.parseLocate(flash.getLocation()), Define.NONE);
    }
}
