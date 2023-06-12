package global.kakao;

import global.auth.CustomAuthorityUtils;
import global.security.JwtTokenizer;
import member.entity.Member;
import member.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;

    private final CustomAuthorityUtils authorityUtils;

    private final MemberService memberService;

    public OAuth2MemberSuccessHandler(JwtTokenizer jwtTokenizer,
                                      CustomAuthorityUtils authorityUtils,
                                      MemberService memberService){

        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException{

        var oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = String.valueOf(oAuth2User.getAttributes().get("email"));
        List<String> authorities = authorityUtils.createRoles(email);

        saveMember(email);
        redirect(request, response, email, authorities);
    }

    private void saveMember(String email){

        Member member = new Member();
        member.setEmail(email);
        memberService.createMember(member);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String username,
                          List<String> authorities) throws IOException{

        String access_token = delegateAccessToken(username, authorities);
        String refresh_token = delegateRefreshToken(username);

        String uri = createURI(access_token, refresh_token).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private String delegateAccessToken(String username, List<String> authorities){

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", authorities);

        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String access_token = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return access_token;
    }

    private String delegateRefreshToken(String username){

        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refresh_token = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refresh_token;
    }

    private URI createURI(String access_token, String refresh_token){

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", access_token);
        queryParams.add("refresh_token", refresh_token);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .path("/receive-token.html")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
