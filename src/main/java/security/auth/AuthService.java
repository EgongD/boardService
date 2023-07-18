package security.auth;

import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import security.jwt.JwtProvider;
import security.token.RefreshTokenRepository;
import security.token.TokenDto;
import user.entity.User;
import user.repositoory.UserRepository;

import java.security.Key;
import java.util.Date;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(JwtProvider jwtProvider, UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository){
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Long getAccessTokenExpiration(String accessToken){
        String jws = accessToken.replace("Bearer ", "");
        String encodedSecretKey = jwtProvider.encodeBase64SecretKey(jwtProvider.getSecretKey());

        Key key = jwtProvider.getKeyFromBase64EncodedKey(encodedSecretKey);

        Date expiration = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws)
                .getBody().getExpiration();

        long now = new Date().getTime();

        return expiration.getTime() - now;
    }

    public void logout(TokenDto.ATNRequest request, String kakaoAccessToken){
        String accessToken = request.getAccessToken();
        String jws = accessToken.replace("Bearer ", "");
        Long userId = jwtProvider.extractMemberId(accessToken);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (user.getProvider().equals("kakao")){

        }
    }
}
