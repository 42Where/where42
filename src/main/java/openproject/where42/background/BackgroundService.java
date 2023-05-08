package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Cluster;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.admin.AdminRepository;
import openproject.where42.token.TokenRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 백그라운드 정보 업데이트를 위한 서비스 클래스
 * @version 1.0
 */
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
    private final AdminRepository adminRepository;
    public String token42;
    public String tokenHane;

    /**
     * <pre>
     *     클러스터에 있는 전체 카뎃들 정보 업데이트
     *     클러스터에 있는 카뎃이 멤버일 경우 해당 멤버 정보에 하네 정보, 42api location 정보 및 updateTime 갱신
     *     멤버가 아닐 경우 플래시데이터 조회
     *     플래시데이터에 존재할 경우 해당 플래시데이터 정보에 42api location 정보 및 updateTime 갱신
     *     플래시데이터에 존재하지 않을 경우 42api 정보 기반으로 플래시데이터 생성
     *     Cluster end_at 정보를 null로만 가져올 수 있을 경우 마지막 if문 변경하여 실제로 아이맥 정보가 있는 카뎃들 정보만 가져오도록 변경 할 수 있음
     * </pre>
     * @see AdminRepository#callAdmin() token DB에 저장된 서버용 토큰 호출
     * @see AdminRepository#callHane() token DB에 저장된 하네용 토큰 호출
     * @see ApiService#get42ClusterInfo(String, int) 클러스터에 있는 모든 카뎃의 정보 조회
     * @see MemberRepository#findByName(String) 멤버 여부 조회
     * @see MemberService#updateBackInfo(Member, Planet, String) 아이맥 정보, 하네 정보 및 updateTime 갱신
     * @see ApiService#getHaneInfo(String, String) 하네 정보 조회
     * @see FlashDataService#findByName(String) 플래시 데이터 존재 여부 조회
     * @see FlashDataService#updateLocation(FlashData, String) 아이맥 정보 및 updateTime 갱산
     * @see FlashDataService#createFlashData(String, String, String) 플래시데이터 생성
     * @since 1.0
     * @author hyunjcho
     */
    public void updateAllInClusterCadet() {
        int i = 1;
        log.info("[updateAllInClusterCadet] Cluster에 있는 cadet의 정보를 업데이트합니다.");
        token42 = adminRepository.callAdmin();
        tokenHane = adminRepository.callHane();
        while(true) {
            List<Cluster> clusterCadets = apiService.get42ClusterInfo(token42, i);
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
            if (clusterCadets.get(99).getEnd_at() != null)
                break;
            i++;
        }
    }

    /**
     * <pre>
     *      아이맥 로그인-아웃 정보 스프링 스케쥴러를 통해 3분 간격 업데이트
     *      해당 메서드는 3분 간격으로 호출되나 42api에서 가져오는 정보는 최근 5분으로 설정하여 정보가 누락되지 않도록 함
     *      42api TooManyRequest 등 오류 발생 시 1초 간격으로 최대 3번 재시도
     * </pre>
     * @see AdminRepository#callAdmin() token DB에 저장된 서버용 토큰 호출
     * @see AdminRepository#callHane() token DB에 저장된 하네용 토큰 호출
     * @see ApiService#get42LocationEnd(String, int) 5분 이내에 로그아웃한 카뎃들의 정보 전체 조회
     * @see ApiService#get42LocationBegin(String, int) 5분 이내에 로그인한 카뎃들의 정보 전체 조회
     * @see MemberService#updateBackInfo(Member, Planet, String) 아이맥 정보, 하네 정보 및 updateTime 갱신
     * @see ApiService#getHaneInfo(String, String) 하네 정보 조회
     * @see ApiService#getHaneInfo(String, String) 하네 정보 조회
     * @see FlashDataService#findByName(String) 플래시 데이터 존재 여부 조회
     * @see FlashDataService#updateLocation(FlashData, String) 아이맥 정보 및  updateTime 갱산
     * @see FlashDataService#createFlashData(String, String, String) 플래시데이터 생성
     * @since 1.0
     * @author hyunjcho
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @Scheduled(cron = "0 0/3 * 1/1 * ?")
    public void update3minClusterInfo() {
        log.info("[Background] 자리 업데이트를 시작합니다");
        token42 = adminRepository.callAdmin();
        tokenHane = adminRepository.callHane();
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

    /**
     * <pre>
     *     update3MinClusterInfo 3번 실패 시 실행되는 메서드
     *     실제 익셉션은 e.getMessage 메소드를 통해 로그로 남기고
     *     throw는 TooManyRequestException으로 통일
     * </pre>
     * @author hyunjcho
     * @since 1.0
     * @param e 던져진 e
     * @throws TooManyRequestException
     */
    @Recover
    public void fallBack(RuntimeException e) {
        log.info("[Background] {}", e.getMessage());
        throw new TooManyRequestException();
    }

    /**
     * 블랙홀에 빠진 카뎃들을 제외 한 모든 카뎃들의 이미지 정보 저장
     * @author hyunjcho
     * @since 1.0
     * @see AdminRepository#callAdmin() token DB에 저장된 서버용 42api 토큰 호출
     * @see ApiService#get42Image(String, int) 이미지 정보 조회
     * @see ImageRepository#inputImage(List) 블랙홀 멤버를 제외한 이미지 DB 저장
     * @see ImageRepository#deduplication() 중복된 이미지 정보 제거
     */
    public void getAllCadetImages() {
        int i = 1;

        token42 = adminRepository.callAdmin();
        while (true) {
            log.info("[Background] Image =={}== 페이지를 부르고 있습니다.", i);
            List<Seoul42> allCadets = apiService.get42Image(token42, i);
            imageRepository.inputImage(allCadets);
            if (allCadets.size() < 100)
                break;
            i++;
        }
        log.info("[Background] 모든 Image 페이지를 불렀습니다.");
        imageRepository.deduplication();
    }
}