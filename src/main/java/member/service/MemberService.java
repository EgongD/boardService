package member.service;

import global.audit.Auditable;
import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import lombok.*;
import member.entity.Member;
import member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){

        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member){

        verifyExistEmail(member.getEmail());

        return memberRepository.save(member);
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
