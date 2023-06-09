package member.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    public static class Post{

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "비밀번호는 영어 대소문자, 숫자 0~9, 특수문자를 포함한 8~16자리여야 합니다.")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{2,10}$",
                message = "닉네임은 영어 대소문자, 숫자 0~9를 포함한 2~10자리여야 합니다.")
        private String nickname;
    }

    @Getter
    @Data
    @NoArgsConstructor
    public static class Patch{

        private Long memberId;

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "비밀번호는 영어 대소문자, 숫자 0~9, 특수문자를 포함한 8~16자리여야 합니다.")
        private String password;

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{2,10}$",
                message = "닉네임은 영어 대소문자, 숫자 0~9를 포함한 2~10자리여야 합니다.")
        private String nickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{

        private Long memberId;

        private String email;

        private String nickname;
    }
}
