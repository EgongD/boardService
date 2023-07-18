package security.oauth.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoOauth {

    private String accessToken;

    private String refreshToken;

    private Integer ATKExpiresIn;

    private Integer RTKExpiresIn;
}
