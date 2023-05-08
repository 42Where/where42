package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.background.ImageRepository;
import openproject.where42.exception.customException.TokenExpiredException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberService;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 검색 관련 API 컨트롤러 클래스
 * @verison 1.0
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final TokenService tokenService;
    private final ApiService apiService;
    private final ImageRepository imageRepository;

    /**
     * <pre>
     *      인자로 넘겨받은 begin으로 시작하는 카뎃 10명 조회
     *      begin의 경우 대문자가 들어올 경우에도 전체 소문자로 변환 후 검색
     *      검색된 카뎃이 검색한 멤버의 친구일 경우 플래그 설정
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 쿠키 저장용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @param begin 검색 시작 단어
     * @return 검색 된 10명 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw with seoul42 Data
     * @see #getEnd(String) 검색 마지막 단어 설정
     * @see #searchCadetInfo(String) 검색 정보 반환 DTO
     * @see MemberRepository#checkFriendByMemberIdAndName(Long, String) 친구 여부 조회
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse res,
                                                  @CookieValue(value = "ID", required = false) String key, @RequestParam("begin") String begin) {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        begin = begin.toLowerCase();
        log.info("[search] \"{}\" 님이 '{}'을 검색하였습니다.", member.getName(), begin);
        List<Seoul42> searchList = apiService.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin());
            if (memberRepository.checkFriendByMemberIdAndName(member.getId(), searchCadet.getName()))
                searchCadet.setFriend(true);
            searchCadetList.add(searchCadet);
        }
       return searchCadetList;
    }

    /**
     * 검색 정보 맴버-플래시 정보 정리, 플래시 데이터에 없을 경우 임시 데이터 생성
     * @param name 정리할 카뎃 인트라 아이디
     * @return 검색 정보 DTO
     * @since 1.0
     * @author hyunjcho
     */
    public SearchCadet searchCadetInfo(String name) {
        Member member = memberRepository.findByName(name);
        if (member != null)
            return new SearchCadet(member);
        FlashData flash = flashDataService.findByName(name);
        if (flash != null)
            return new SearchCadet(flash);
        return new SearchCadet(name, imageRepository.findByName(name));
    }

    /**
     * <pre>
     *     검색 종료 단어 생성
     *     1. begin이 z로 시작하지 않으면서 z로 끝날 경우: 시작 단어 첫 글자의 다음 알파벳
     *      이 경우 인트라 아이디가 한 글자인 경우는 없기 때문에 정확히 존재하는 인트라 아이디만 검색
     *      ex) hyuz -> i
     *     2. begin이 z로 시작하고 z로 끝날 경우: 시작단어 + z
     *      이 경우 zzzz가 10명내에 존재하더라도 검색되지 않음
     *      ex) za -> zzz
     *     3. begin의 시작과 끝이 모두 z가 아닌 경우: 시작 단어 마지막 글자를 다음 알파벳으로 변경
     *      이 경우 마지막 글자의 인트라 아이디가 존재할 경우 같이 검색됨
     *      ex) hyun -> hyum
     *     인트라 아이디의 길이가 정해져있다면 남은 글자수를 전부 z로 채우는 것도 방법이 될 수 있음
     * </pre>
     * @param begin 검색 시작 단어
     * @return 검색 종료 단어
     * @since 1.0
     * @author hyunjcho
     */
    private String getEnd(String begin) {
        char first = begin.charAt(0);
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z')
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z')
            return begin + "z";
        else
            return begin.substring(0, begin.length() - 1) + (char)((int) last + 1);
    }

    /**
     * <pre>
     *      검색된 카뎃 선택시 정보 파싱후 반환
     *      이 경우 인자로 넘겨받은 cadet은 /search api에서 파싱된 정보를 front_end가 넘겨받은 후 다시 넘겨 준 정보로
     *      location이 파싱되어있지 않을 경우에만 파싱 진행
     * </pre>
     * @param cadet 정보 확인 할 카뎃
     * @return 정보 정리된 카뎃 정보
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        if (cadet.isMember()) {
            Member member = memberRepository.findByName(cadet.getName());
            memberService.checkMemberStatus(member);
            cadet.updateStatus(member.getLocate(), member.getInOrOut());
        }
        else {
            if (!Define.PARSED.equalsIgnoreCase(cadet.getLocation())) {
                FlashData flash = flashDataService.findByName(cadet.getName());
                if (!Define.PARSED.equalsIgnoreCase(flash.getLocation()))
                    flashDataService.parseStatus(flash);
                cadet.updateStatus(flash.getLocate(), flash.getInOrOut());
            }
        }
        return cadet;
    }
}