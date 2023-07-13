package security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import security.auth.PrincipalDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException{
        System.out.println("JwtAuthentication : 로그인 시도중");

        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;

        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("JwtAuthenticationFilter : " + loginRequestDto);

        // 유저네임 패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        System.out.println("JwtAuthenticationFilter : 토큰 생성 완료");

        // authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의 loadUserByUsername(토큰의 첫번째 파라미터)을 호출하고,
        // UserDetails를 리턴받아서 토큰의 두번째 파라미터(credential)와 UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
        // Authentication 객체를 만들어서 필터페인으로 리턴해준다.

        // Tip : 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
        // Tip : 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
        // 인증 프로바이더의 알려줄 필요가 없다.
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("Authentication : " + principalDetails.getUser().getUsername());

        return authentication;
    }

    // attemptAuthentication 실행 후 정상적으로 인증되면 successfulAuthentication 함수가 실행 됨
    // JWT토큰을 만들어서 request 요청한 사용자에게 JWT토큰을 response해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authResult)
        throws IOException, ServletException{

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getUserId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        System.out.println("인증완료");
        super.successfulAuthentication(request, response, filterChain, authResult);
    }
}
