package openproject.where42.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.AdminLoginFailException;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.groupFriend.GroupFriend;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.entity.Administrator;
import openproject.where42.member.entity.Member;
import openproject.where42.util.SearchCadet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 기능 서비스 클래스
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final GroupFriendRepository groupFriendRepository;
    private final ApiService apiService;
    private final AdminApiService adminApiService;

    /**
     * <pre>
     *     세션에 등록된 이름을 통해 관리자 검색
     *     세션이 있을 경우 세션 타임 연장
     * </pre>
     * @author hyunjcho
     * @since 1.0
     * @param req 세션 확인용 HttpServletRequest
     * @return 해당 관리자가 존재하는 지 확인 후 T/F 반환
     * @throws SessionExpiredException 세션을 찾을 수 없는 경우 401 예외 throw
     * @see openproject.where42.admin.AdminRepository#findByAdminName(String) 관리자 조회
     * @since 1.0
     * @author hyunjcho
     */
    public boolean findAdminBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        session.setMaxInactiveInterval(60 * 60);
        return adminRepository.findByAdminName((String)session.getAttribute("name"));
    }

    /**
     * 인자로 받은 name과 passwd가 일치하는 관리자가 있는지 조회
     * @author hyunjcho
     * @since 1.0
     * @param name 찾고자 하는 관리자 이름
     * @param passwd 해당 관리자의 비밀번호
     * @throws AdminLoginFailException 일치하는 관리자가 없을 경우 401 예외 throw
     * @see openproject.where42.admin.AdminRepository#adminLogin(String, String) 관리자 로그인
     * @since 1.0
     * @author hyunjcho
     */
    public void adminLogin(String name, String passwd) {
        boolean login = adminRepository.adminLogin(name, passwd);
        if (!login)
            throw new AdminLoginFailException();
    }

    /**
     * <pre>
     *     기수별 신규 네트워크망 확산 현황 확인을 위해 피신 등록일 멤버 데이터 추가
     *     신규 회원의 경우 가입시 정보가 저장되며, 정보가 없는 기존 회원 업데이트용 메소드
     * </pre>
     * @see MemberRepository#allMember() 전체 멤버 조회
     * @see AdminRepository#callAdmin() token DB에 저장된 서버용 토큰 호출
     * @see Member#updateSignUpDate(String) 피신 시작일 갱신
     * @since 2.0
     * @author sunghkim
     */
    @Transactional
    public void getSignUpDate() {
        List<GroupFriend> friends = groupFriendRepository.findAll();
        List<Member> list = memberRepository.allMember();
        String token42 = adminRepository.callAdmin();
        if (list == null || list.size() == 0)
            return ;
        log.info("[Background] SignUpDate를 시작합니다.");
        for (Member i : list) {
            log.info("[Background] {} SignUpDate를 등록합니다.", i);
            if (i.getSignUpDate() == null) {
                Seoul42 cadet = adminApiService.adminGetUserInfo(token42, i.getName());
                i.updateSignUpDate(cadet.getCreated_at());
            }
        }
        int j = 0;
        System.out.println(friends);
        if (friends != null){
            for (GroupFriend i : friends){
                if (i.getSignUpDate() != null)
                    continue;
                log.info("[getSignUpdate] {} 님의 SignUpdate를 갱신중입니다", i.getFriendName());
                i.updateSignUpDate(adminApiService.adminGetUserInfo(token42, i.getFriendName()).getCreated_at());
                if (j == 100)
                    break;
                j++;
            }
        }
        log.info("[Background] SignUpDate가 끝났습니다.");
    }

    /**
     * <pre>
     *     인자로 받은 이름의 멤버 삭제
     *     관리자 함수로 따로 멤버가 존재하는지 여부에 대한 예외처리 하지 않음
     * </pre>
     * @param name 삭제할 멤버 이름
     * @see openproject.where42.member.MemberRepository#deleteMember(String)
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void deleteMember(String name) {
        log.info("[member-delete] \"{}\"님이 멤버에서 삭제되었습니다.", name);
        memberRepository.deleteMember(name);
    }

    /**
     * <pre>
     *     "어디있니" 검색 이스터 에그용!
     *     public SearchCadet(String name, String img, String msg, String spot)
     *     해당 생성자 사용하여 정보 반환
     * </pre>
     * @param admins 관리자 배열
     * @return 관리자 검색 DTO 배열 반환
     * @see SearchCadet
     */
    public static ArrayList<SearchCadet> where42(List<Administrator> admins) {
        ArrayList<SearchCadet> where42s = new ArrayList<>();
        for (Administrator admin : admins)
            where42s.add(new SearchCadet(admin.getName(), admin.getImg(), admin.getMsg(), admin.getSpot()));
        return where42s;
    }
}
