package security.config;

import global.CustomAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import security.auth.JwtVerificationFilter;
import security.auth.UserAuthenticationFailureHandler;
import security.auth.UserAuthenticationSuccessHandler;
import security.jwt.JwtAuthenticationFilter;
import security.jwt.JwtProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final CustomAuthorityUtils authorityUtils;

    public SecurityConfig(JwtProvider jwtProvider, CustomAuthorityUtils authorityUtils){
        this.jwtProvider = jwtProvider;
        this.authorityUtils = authorityUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .cors().configurationSource(source())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeRequests(auth ->
                        auth
                                .antMatchers(HttpMethod.POST, "/users").permitAll()
                                .antMatchers(HttpMethod.PATCH, "/users/*").hasRole("ROLE_USER")
                                .antMatchers(HttpMethod.GET, "/users/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/users/*").hasRole("ROLE_ADMIN")
                                .antMatchers(HttpMethod.POST, "/posts").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.GET, "/posts/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.PATCH, "/posts/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/posts/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.POST, "/comments").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.PATCH, "/comments/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/comments/*").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource source(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity>{

        @Override
        public void configure(HttpSecurity security) throws Exception{
            AuthenticationManager authenticationManager = security.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtProvider);

            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtProvider, authorityUtils);

            security.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtVerificationFilter.class);
        }
    }
}
