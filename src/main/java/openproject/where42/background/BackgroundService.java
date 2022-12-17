package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Cluster;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@EnableScheduling
@Transactional
@Service
public class BackgroundService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final ApiService apiService;
    private final TokenRepository tokenRepository;
    private final ImageRepository imageRepository;
    public String token42;

    public void updateAllInClusterCadet() { // 낮밤을 바꿀 것인지?
        int i = 1;
        token42 = tokenRepository.callAdmin();
        while(true) {
            CompletableFuture<List<Cluster>> cf = apiService.get42ClusterInfo(token42, i);
            List<Cluster> clusterCadets = apiService.injectInfo(cf);
            for (Cluster cadet : clusterCadets) {
                Member member = memberRepository.findMember(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateLocation(member, cadet.getUser().getLocation());
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

    @Retryable(maxAttempts = 1, backoff = @Backoff(1000))
    @Scheduled(cron = "0 0/3 * 1/1 * ?")
    public void update3minClusterInfo() {
        token42 = tokenRepository.callAdmin();
        Date date = new Date();
        System.out.println("3분이 지났어요!!! " + date);
        int i = 1;
        while (true) {
            List<Cluster> logoutCadets = apiService.get42LocationEnd(token42, i);
            for (Cluster cadet : logoutCadets) {
                Member member = memberRepository.findMember(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateLocation(member, cadet.getUser().getLocation());
                else {
                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
                    if (flash != null)
                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
                    else
                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
                }
            }
//            for (Cluster cluster : logoutCadets)
//                System.out.println("** end name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at() + " begin at = " + cluster.getBegin_at());
            if (logoutCadets.size() < 100)
                break;
            i++;
        }
        i = 1;
        while(true) {
            List<Cluster> loginCadets = apiService.get42LocationBegin(token42, i);
            for (Cluster cadet : loginCadets) {
                Member member = memberRepository.findMember(cadet.getUser().getLogin());
                if (member != null)
                    memberService.updateLocation(member, cadet.getUser().getLocation());
                else {
                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
                    if (flash != null)
                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
                    else
                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
                }
            }
//            for (Cluster cluster : clusterCadets)
//                System.out.println("** begin name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at() + " begin at = " + cluster.getBegin_at());
            if (loginCadets.size() < 100)
                break;
            i++;
        }
    }

    @Recover
    public void fallBack(RuntimeException e) {
        System.out.println("==== Background is doomed ====");
        e.printStackTrace();
        throw new TooManyRequestException();
    }

    public void deleteMemberImage() {
        imageRepository.deleteMember();
    }

    public void getAllCadetImages() {
        token42 = tokenRepository.callAdmin();
        int i = 1;
        while (true) {
            CompletableFuture<List<Seoul42>> cf = apiService.get42Image(token42, i);
            List<Seoul42> allCadets = apiService.injectInfo(cf);
            imageRepository.inputImage(allCadets);
            if (allCadets.size() < 100)
                break;
            System.out.println("DONE");
            i++;
        }
        imageRepository.deduplication();
    }
}