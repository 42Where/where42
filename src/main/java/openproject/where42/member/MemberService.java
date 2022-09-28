package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findOne(String name) {
        return memberRepository.findOne(name);
    }
}
