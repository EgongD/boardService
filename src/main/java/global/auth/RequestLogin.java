package global.auth;

import lombok.*;

import javax.validation.constraints.Size;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogin {

    @NonNull
    @Size(min = 3, max = 50)
    private String email;

    @NonNull
    @Size(min = 5, max = 100)
    private String password;
}
