package security.jwt;

import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import security.token.RefreshToken;
import security.token.RefreshTokenRepository;
import security.token.TokenDto;
import user.entity.User;
import user.repositoory.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component("jwtProvider")
public class JwtProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public JwtProvider(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public Date makeToExpiration(int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    public String encodeBase64SecretKey(String secretKey){

        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Key getKeyFromBase64EncodedKey(String encodedSecretKey){
        byte[] keyBytes = Decoders.BASE64URL.decode(encodedSecretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(User user){
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("username", user.getEmail());
        payloads.put("roles", user.getRoles());

        Date expiration = makeToExpiration(getAccessTokenExpirationMinutes());

        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject(user.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(User user){
        Date expiration = makeToExpiration(getRefreshTokenExpirationMinutes());

        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        String tokenValue = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        RefreshToken refreshToken = new RefreshToken(tokenValue, user.getUserId());
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    public TokenDto.RTNResponse generateAccessTokenWithRefreshToken(Long userId, String refreshTokenValue){
        RefreshToken refreshToken = refreshTokenRepository.findById(userId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND));

        if (!refreshTokenValue.equals(refreshToken.getRefreshToken()))
            throw new BusinessLogicException(ExceptionCode.DIFFERENT_REFRESHTOKEN);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

        String accessToken = createAccessToken(user);

        return new TokenDto.RTNResponse(userId, accessToken);
    }

    public Jws<Claims> getClaims(String jws, String encodedKey){
        Key key = getKeyFromBase64EncodedKey(encodedKey);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    public Long extractMemberId(String accessToken){
        String jws = accessToken.replace("Bearer ", "");
        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody()
                    .getSubject();

            return userRepository.findByEmail(email).orElseThrow(
                    () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
            ).getUserId();
        } catch (JwtException e){
            throw new JwtException("Invalid AccessToken");
        }
    }

    public boolean validateToken(String token){
        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String subject = claims.getBody().getSubject();

            return true; // 토큰이 유효하면 true
        } catch (JwtException e){
            return false; // 토큰이 유효하지 않으면 false
        }
    }
}
