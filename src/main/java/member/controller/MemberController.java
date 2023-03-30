package member.controller;

import global.security.JwtProvider;
import member.dto.MemberDto;
import member.entity.Member;
import member.mapper.MemberMapper;
import member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MemberController {

    private final MemberService memberService;

    private final MemberMapper memberMapper;

    private final JwtProvider jwtProvider;

    public MemberController(MemberService memberService,
                            MemberMapper memberMapper,
                            JwtProvider jwtProvider){

        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> postMember(@RequestBody MemberDto.Post post){

        Member member = memberMapper.memberPostToMember(post);

        return new ResponseEntity<>(
                memberMapper.memberToMemberResponseDto(memberService.createMember(member)),
                        HttpStatus.CREATED);
    }

    @PatchMapping("/auth")
    public ResponseEntity<?> patchMember(@RequestBody MemberDto.Patch patch,
                                         @RequestHeader("Authorization") String accessToken){

        Long memberId = jwtProvider.extractMemberId(accessToken);
        patch.setMemberId(memberId);
        Member member = memberService.updateMember(memberMapper.memberPatchToMember(patch));

        return new ResponseEntity<>(memberMapper.memberToMemberResponseDto(member), HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<?> getMember(@RequestHeader("Authrization") String accessToken){

        Long memberId = jwtProvider.extractMemberId(accessToken);
        Member member = memberService.findMember(memberId);

        return new ResponseEntity<>(
                memberMapper.memberToMemberResponseDto(member), HttpStatus.OK);
    }


    @GetMapping("/auth/all")
    public ResponseEntity<?> getMembers(){

        return new ResponseEntity<>(memberMapper.membersToMemberResponseDtos(memberService.findMembers()),
                HttpStatus.OK);
    }

    @DeleteMapping("/auth/{member-id}")
    public ResponseEntity deleteMember(@RequestHeader("Authorization") String accessToken){

        Long memberId = jwtProvider.extractMemberId(accessToken);
        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
