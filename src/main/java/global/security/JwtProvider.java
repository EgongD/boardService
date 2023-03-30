package global.security;

import global.exception.BusinessLogicException;
import global.exception.ExceptionCode;
import global.security.token.RefreshToken;
import global.security.token.RefreshTokenRepository;
import global.security.token.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import lombok.Getter;
import member.entity.Member;
import member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component("jwtProvider")
public class JwtProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final BlackListRepository blackListRepository;

    public JwtProvider(RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository,
                       BlackListRepository blackListRepository){

        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.blackListRepository = blackListRepository;
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

    public Date makeTokenExpiration(int minutes){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    public String encodeBase64SecretKey(String secretKey){

        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Key getKeyFromBase64EncodedKey(String encodedSecretKey){

        byte[] keyBytes = Decoders.BASE64.decode(encodedSecretKey);

        return keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Member member){

        Map<String, Objects> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("username", member.getEmail());
        payloads.put("roles", member.getRoles());

        Date expiration = makeTokenExpiration(getAccessTokenExpirationMinutes());

        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject(member.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Member member){

        Date expiration = makeTokenExpiration(getRefreshTokenExpirationMinutes());

        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        String tokenValue = Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        RefreshToken refreshToken = new RefreshToken(tokenValue, member.getMemberId());
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    public TokenDto.RTNResponse genereteAccessTokenWithRefreshToken(Long memberId, String refreshTokenValue){

        RefreshToken refreshToken = refreshTokenRepository.findById(memberId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND)
        );

        if(!refreshTokenValue.equals(refreshToken.getRefreshToken()))
            throw new BusinessLogicException(ExceptionCode.DIFFERENT_REFRESHTOKEN);

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
        );

        String accessToken = createAccessToken(member);

        return new TokenDto.RTNResponse(memberId, accessToken);
    }

    public Jws<Claims> getClaims(String jms, String encodedKey){

        Key key = getKeyFromBase64EncodedKey(encodedKey);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jms);
    }

    public Long extractMemberId(String accessToken){

        String jws = accessToken.replace("Bearer", "");
        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        try{
            String email = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody()
                    .getSubject();

            return memberRepository.findByEmail(email).orElseThrow(
                    () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
            ).getMemberId();
        } catch (JwtException e){

            throw new JwtException("Invalid AccessToken");
        }
    }

    public boolean validateToken(String token){

        String encodedSecretKey = encodeBase64SecretKey(getSecretKey());
        Key key = getKeyFromBase64EncodedKey(encodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        return blackListRepository.findById(token).isPresent();
    }
}
