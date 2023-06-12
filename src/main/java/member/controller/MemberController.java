package member.controller;

import member.dto.MemberDto;
import member.entity.Member;
import member.mapper.MemberMapper;
import member.repositoory.MemberRepository;
import member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    private final MemberMapper memberMapper;

    private final MemberRepository memberRepository;

    public MemberController(MemberService memberService,
                            MemberMapper memberMapper,
                            MemberRepository memberRepository){

        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post post){

        Member member = memberService.createMember(memberMapper.memberPostToMember(post));

        MemberDto.Response memberPostResponse = memberMapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(memberPostResponse, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity patchMember(@Valid @RequestBody MemberDto.Patch patch){

        Member member = memberService.updateMember(memberMapper.memberPatchToMember(patch));

        MemberDto.Response memberPatchResponse = memberMapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(memberPatchResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMember(@Positive @PathVariable("member-id") Long memberId){

        Member member = memberService.findMember(memberId);

        MemberDto.Response memberGetResponse= memberMapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(memberGetResponse, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive Long memberId){

        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
