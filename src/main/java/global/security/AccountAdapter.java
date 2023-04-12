package global.security;

import global.auth.Authority;
import member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private Member member;

    public AccountAdapter(Member member){

        super(member.getNickname(), member.getPassword(), authorities(member.getAuthorities()));

        this.member = member;
    }

    public Member getMember(){

        return this.member;
    }

    private static List<GrantedAuthority> authorities(Set<Authority> authorities){

        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
    }
}
