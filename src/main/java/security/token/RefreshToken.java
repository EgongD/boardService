package security.token;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 2 * 60 * 60)
public class RefreshToken {

    private String refreshToken;

    @Id
    private Long userId;

    public RefreshToken(String refreshToken, Long userId){
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
