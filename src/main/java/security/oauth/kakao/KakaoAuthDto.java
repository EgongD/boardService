package security.oauth.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import security.oauth.provider.Oauth2UserInfo;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoAuthDto {

    private Long userId;

    private String accessToken;

    private String refreshToken;

    private String kakaoAccessToken;
}
