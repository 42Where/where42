package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.domain.enums.Place;
import openproject.where42.member.repository.MemberRepository;
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

        member.updatePersonalMsg(msg);
    }

    @Transactional
    public void updateLocate(Long memberId, Planet planet, int floor, int cluster, Place place) {
        Member member = memberRepository.findById(memberId);

        member.updateLocate(planet, floor, cluster, place);
    }

}
