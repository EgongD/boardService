package security.token;

import jdk.dynalink.beans.StaticClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDto {

    @Getter
    @NoArgsConstructor
    public static class RTNRequest{
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    public static class RTNResponse{
        private Long userId;

        private String accessToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ATNRequest{
        private String accessToken;
    }
}
