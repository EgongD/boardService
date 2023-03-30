package global.security.token;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 2 * 60 * 60)
public class RefreshToken {

    private String refreshToken;

    @Id
    private Long memberId;

    public RefreshToken(String refreshToken, Long memberId){

        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
