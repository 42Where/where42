package openproject.where42.api;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Cluster;
import openproject.where42.member.FlashDataService;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@EnableScheduling
@Transactional
public class ClusterService {
    public static MemberService memberService;
    public static MemberRepository memberRepository;
    public static FlashDataService flashDataService;
    public static ApiService apiService;
    public static TokenService tokenService;
    public static Date lastUpdateTime;

    // 백그라운드 업데이트
//    @Scheduled(cron = "0 ") 2주에 한 번.. 은 어떻게 못하겠는 걸... 수동..?
    public void updateAllOccupyingCadet(String token42) { // 낮밤을 바꿀 것인지?
        int i = 0;
        while(true) {
            CompletableFuture<List<Cluster>> cf = apiService.get42OccupyingInfo(token42, i); // 여기 토큰 필요 갱신이 필요
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
            for (Cluster cluster : clusterCadets) {
                System.out.println("** name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at());
            }
        }
    }

    @Scheduled(cron = "0 0/3 * 1/1 * ? *")
    public void update3minOccupyingCadet() {
        String token42 = tokenService.findAccessToken(token42); // 토큰 가져오기
        int i = 1;
        while (true) {
            CompletableFuture<List<Cluster>> cf = apiService.get42LocationEnd(token42, i);
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
            for (Cluster cluster : clusterCadets)
                System.out.println("** end name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at() + " begin at = " + cluster.getBegin_at());
            if (clusterCadets.size() < 50)
                break;
            i++;
        }
        i = 1;
        while(true) {
            CompletableFuture<List<Cluster>> cf = apiService.get42LocationBegin(token42, i); // 여기 토큰 필요 갱신이 필요
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
            for (Cluster cluster : clusterCadets)
                System.out.println("** begin name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at() + " begin at = " + cluster.getBegin_at());
            if (clusterCadets.size() < 50)
                break;
            i++;
        }
    }
}