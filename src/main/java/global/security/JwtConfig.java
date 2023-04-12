package global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInSeconds;

    @Bean(name = "tokenProvider")
    public TokenProvider tokenProvider(){

        return new TokenProvider(accessTokenSecret, accessTokenValidityInSeconds);
    }
}
