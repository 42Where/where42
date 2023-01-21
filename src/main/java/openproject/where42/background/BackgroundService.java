package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Cluster;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.flashData.FlashData;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@EnableScheduling
@Transactional
@Service
@Slf4j
public class BackgroundService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final ApiService apiService;
    private final TokenRepository tokenRepository;
    private final ImageRepository imageRepository;
    public String token42;
    public String tokenHane;

    public void updateAllInClusterCadet() { // 낮밤을 바꿀 것인지?
        int i = 1;
        log.info("[updateAllInClusterCadet] Cluster에 있는 cadet의 정보를 업데이트합니다.");
        token42 = tokenRepository.callAdmin();
        tokenHane = tokenRepository.callHane();
        while(true) {
            CompletableFuture<List<Cluster>> cf = apiService.get42ClusterInfo(token42, i);
            List<Cluster> clusterCadets = apiService.injectInfo(cf);
            for (Cluster cadet : clusterCadets) {
                Member member = memberRepository.findByName(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateBackInfo(member, apiService.getHaneInfo(member.getName(), tokenHane), cadet.getUser().getLocation());
                else {
                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
                    if (flash != null)
                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
                    else
                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
                }
            }
            if (clusterCadets.get(49).getEnd_at() != null) //null로 할 수 있다면! 이거 조건 뺴도 됨!
                break;
            i++;
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @Scheduled(cron = "0 0/3 * 1/1 * ?")
    public void update3minClusterInfo() {
        log.info("[Background] 자리 업데이트를 시작합니다");
        token42 = tokenRepository.callAdmin();
        tokenHane = tokenRepository.callHane();
        int i = 1;
        while (true) {
            List<Cluster> logoutCadets = apiService.get42LocationEnd(token42, i);
            for (Cluster cadet : logoutCadets) {
                Member member = memberRepository.findByName(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateBackInfo(member, apiService.getHaneInfo(member.getName(), tokenHane), cadet.getUser().getLocation());
                else {
                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
                    if (flash != null)
                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
                    else
                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
                }
                log.info("[Background] logout name = {} location = {}",
                        cadet.getUser().getLogin(),
                        cadet.getUser().getLocation()
                );
            }
            if (logoutCadets.size() < 100)
                break;
            i++;
        }
        i = 1;
        while(true) {
            List<Cluster> loginCadets = apiService.get42LocationBegin(token42, i);
            for (Cluster cadet : loginCadets) {
                Member member = memberRepository.findByName(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateBackInfo(member, apiService.getHaneInfo(member.getName(), tokenHane), cadet.getUser().getLocation());
                else {
                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
                    if (flash != null)
                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
                    else
                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
                }
                log.info("[Background] login name = {} location = {}",
                        cadet.getUser().getLogin(),
                        cadet.getUser().getLocation()
                );
            }
            if (loginCadets.size() < 100)
                break;
            i++;
        }
        log.info("[Background] 업데이트를 완료하였습니다.");
    }

    @Recover
    public void fallBack(RuntimeException e) {
        log.info("[Background] {}", e.getMessage());
        e.printStackTrace();
        throw new TooManyRequestException();
    }

    public void getAllCadetImages() {
        token42 = tokenRepository.callAdmin();
        int i = 1;
        while (true) {
            log.info("[Background] Image =={}== 페이지를 부르고 있습니다.", i);
            CompletableFuture<List<Seoul42>> cf = apiService.get42Image(token42, i);
            List<Seoul42> allCadets = apiService.injectInfo(cf);
            imageRepository.inputImage(allCadets);
            if (allCadets.size() < 100)
                break;
            i++;
        }
        log.info("[Background] 모든 Image 페이지를 불렀습니다.");
        imageRepository.deduplication();
    }
}