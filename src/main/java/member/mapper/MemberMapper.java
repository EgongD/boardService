package member.mapper;

import member.dto.MemberDto;
import member.entity.Member;

import java.util.List;

public interface MemberMapper {

    Member memberPostToMember(MemberDto.Post post);

    Member memberPatchToMember(MemberDto.Patch patch);

    MemberDto.Response memberToMemberResponseDto(Member member);

    List<MemberDto.Response> membersToMemberResponseDtos(List<Member> members);
}
