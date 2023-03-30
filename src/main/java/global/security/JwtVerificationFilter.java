package global.security;

import org.springframework.web.filter.OncePerRequestFilter;

public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final CustomAuthorityUtils customAuthorityUtils;

    public JwtVerificationFilter(JwtProvider jwtProvider,
                                 CustomAuthorityUtils customAuthorityUtils){

        this.jwtProvider = jwtProvider;
        this.customAuthorityUtils = customAuthorityUtils;
    }
}
