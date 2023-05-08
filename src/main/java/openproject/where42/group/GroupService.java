package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.DefaultGroupNameException;
import openproject.where42.exception.customException.DuplicateGroupNameException;
import openproject.where42.exception.customException.BadRequestException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 그룹 관련 서비스 클래스
 * @version 1.0
 * @see openproject.where42.group
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    /**
     * <pre>
     *     멤버 기본 및 즐겨찾기 그룹 생성
     *     멤버가 생성 될 시 바로 호출됨
     * </pre>
     * @param member 그룹 생성해야할 멤버
     * @param groupName 생성할 그룹 이름
     * @return 생성한 그룹 아이디
     * @throws DefaultGroupNameException 기본 및 즐겨찾기가 아닌 이름이 들어올 경우 409 예외가 throw 되도록 하였으나, 프론트에서 호출되지 않는 함수로 사실상 예외 처리가 크게 유의미하지 않으나 예방용 예외 추가
     * @see openproject.where42.group.GroupRepository#save(Groups) 그룹 저장
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public Long createDefaultGroup(Member member, String groupName) {
        if (!(groupName.equalsIgnoreCase("기본") || groupName.equalsIgnoreCase("즐겨찾기")))
            throw new DefaultGroupNameException();
        return groupRepository.save(new Groups(groupName, member));
    }

    /**
     * 멤버 커스텀 그룹 생성
     * @param groupName 생성할 그룹 이름
     * @param owner 그룹 오너 멤버
     * @return 생성한 그룹 아이디
     * @throws DuplicateGroupNameException
     * @see #validateDuplicateGroupName(Long, String) 그룹 이름 검증
     * @see openproject.where42.group.GroupRepository#save(Groups) 그룹 저장
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public Long createCustomGroup(String groupName, Member owner) {
        validateDuplicateGroupName(owner.getId(), groupName);
        return groupRepository.save(new Groups(groupName, owner));
    }

    /**
     * 멤버의 기본 및 즐겨찾기 그룹을 제외한 모든 커스텀 그룹 정렬 검색
     * @param ownerId 조회하고자 하는 멤버 아이디
     * @return 멤버의 기본 및 즐겨찾기 그룹을 제외한 모든 커스텀 그룹을 정렬 후 반환
     * @see GroupRepository#findGroupsByOwnerId(Long) 커스텀 그룹 조회
     * @since 1.0
     * @author hyunjcho
     */
    public List<Groups> findAllGroupsExceptDefault(Long ownerId) {
        return groupRepository.findGroupsByOwnerId(ownerId);
    }

    /**
     * <pre>
     *      세션 및 토큰을 통해 멤버 검색
     *      세션이 있는 경우 세션 시간 연장
     *      세션이 파기된 경우 토큰을 통해 멤버를 검색 후 세션 생성
     *      순환 참조로 인해 멤버서비스 메서드를 사용할 수 없어 그룹 서비스에 추가로 작성
     * </pre>
     * @param req 멤버 세션 확인용 HttpServletRequest
     * @param res 멤버 토큰 확인용 HttpServletResponse
     * @param key 멤버 토큰 확인용 쿠키값
     * @return 세션 혹은 토큰을 통해 찾은 멤버 반환
     * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
     * @see openproject.where42.member.MemberRepository#findIdByToken(String) 토큰을 통해 멤버 조회
     * @see openproject.where42.member.MemberRepository#findById(Long) 아이디를 통해 멤버 조회
     * @since 1.0
     * @author hyunjcho
     */
    public Member findOwnerBySessionWithToken(HttpServletRequest req, HttpServletResponse res, String key) {
        String token42 = tokenService.findAccessToken(res, key);
        HttpSession session = req.getSession(false);
        if (session == null) {
            Long memberId = memberRepository.findIdByToken(token42);
            if (memberId == 0)
                throw new UnregisteredMemberException();
            req.getSession();
            session.setAttribute("id", memberId);
        }
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    /**
     * 인자로 받은 그룹의 그룹 이름 갱신
     * @param groupId 이름을 수정할 그룹 아이디
     * @param groupName 수정할 그룹 이름
     * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
     * @throws DuplicateGroupNameException 이미 존재하는 그룹 이름일 경우 409 예외 throw
     * @see openproject.where42.group.GroupRepository#findById(Long) groupId로 그룹 조회
     * @see #validateDuplicateGroupName(Long, String) 그룹 이름 검증
     * @see openproject.where42.group.Groups#updateGroupName(String) 그룹 이름 갱신
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);
        if (group == null)
            throw new BadRequestException();
        if (group.getGroupName().equals(groupName))
            return ;
        validateDuplicateGroupName(group.getOwner().getId(), groupName);
        group.updateGroupName(groupName);
    }

    /**
     * 인자로 받은 그룹 이름이 인자로 받은 오너의 기존 그룹에 존재하는 지 여부 조회
     * @param ownerId 그룹 오너 아이디
     * @param groupName 검사할 그룹 이름
     * @throws DuplicateGroupNameException 중복된 그룹이 존재할 경우 409 예외 throw
     * @see openproject.where42.group.GroupRepository#isGroupNameInOwner(Long, String) 오너가 가진 그룹이름 중 groupName이 존재하는지 조회
     * @since 1.0
     * @author hyunjcho
     */
    private void validateDuplicateGroupName(Long ownerId, String groupName) {
        if (groupRepository.isGroupNameInOwner(ownerId, groupName))
            throw new DuplicateGroupNameException();
    }

    /**
     * 인자로 받은 그룹 삭제
     * @param groupId 삭제할 그룹 아이디
     * @throws BadRequestException 존재하지 않는 그룹일 경우 400 예외 throw
     * @see openproject.where42.group.GroupRepository#deleteByGroupId(Long) 그룹 삭제
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void deleteByGroupId(Long groupId) {
        if (!groupRepository.deleteByGroupId(groupId))
            throw new BadRequestException();
    }
}