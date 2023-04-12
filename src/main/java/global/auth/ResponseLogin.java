package global.auth;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLogin {

    private String accessToken;
}
