package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updatePersonalMsg(Long memberId, String msg) {
        Member member = memberRepository.findById(memberId);

        member.updatePersonalMsg(msg); // 이거는 db층 까지 갈 필요가 없나? 예외처리?
    }

    @Transactional
    public void updateLocate(Long memberId, Cluster cluster, Floor floor, Locate locate) {
        Member member = memberRepository.findById(memberId);

        member.updateLocate(cluster, floor, locate); // 이거는 db층 까지 갈 필요가 없나? 예외처리?
    }

}
