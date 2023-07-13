package security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import security.auth.PrincipalDetails;
import user.entity.User;
import user.repositoory.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository){
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
        throws IOException, ServletException{
        String header = request.getHeader(JwtProperties.HEADER_STRING);

        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)){
            filterChain.doFilter(request, response);

            return;
        }

        System.out.println("header : " + header);
        String token = request.getHeader(JwtProperties.HEADER_STRING)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        // 토큰 검증
        // SecurityContext에 접근해서 세션을 만들 때 자동으로 UserDetailsService에 있는 loadByUsername이 호출됨.
        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                .getClaim("username").asString();

        if (username != null){
            User user = userRepository.findByUserName(username);

            // 인증은 토큰 검증시 끝난다.
            // 인증을 하기 위해서가 아닌 Spring Security가 수행해주는 권한 처리를 위해 토큰을 만들어서
            // Authentication 객체를 강제로 만들고 그걸 세션에 저장한다.
            PrincipalDetails principalDetails = new PrincipalDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, // 나중에 컨트롤러에서 DI하기 편하다.
                    null, // 패스워드는 모르니까 null 처리한다.
                    principalDetails.getAuthorities()
            );

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
