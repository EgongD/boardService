package member.service;

import global.event.MemberRegistrationApplicationEvent;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import global.auth.CustomAuthorityUtils;
import member.entity.Member;
import member.repositoory.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomAuthorityUtils authorityUtils;

    private final ApplicationEventPublisher publisher;


    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         CustomAuthorityUtils authorityUtils,
                         ApplicationEventPublisher publisher){

        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
        this.publisher = publisher;
    }

    public Member createMember(Member member){

        verifyExistEmail(member.getEmail());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        Member savedMember = memberRepository.save(member);

        publisher.publishEvent(new MemberRegistrationApplicationEvent(this, savedMember));

        return savedMember;
    }

    public Member updateMember(Member member) {

        Member findMember = findExistedMember(member.getMemberId());

        Optional.ofNullable(member.getNickname()).ifPresent(findMember::setNickname);

        return memberRepository.save(findMember);
    }

    public Member findMember(Long memberId){

        Member member = findExistedMember(memberId);

        return member;
    }

    // 전체 사용자 조회
    public List<Member> findMembers(){

        return memberRepository.findAll();
    }

    public void deleteMember(Long memberId){

        memberRepository.delete(findExistedMember(memberId));
    }

    private void verifyExistEmail(String email){

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXIST);
        }
    }

    private Member findExistedMember(Long memberId){

        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
        );
    }
}
