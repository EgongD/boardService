package board.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class BoardDto {

    @Getter
    @NoArgsConstructor
    public static class Post {

        @NotBlank(message = "글의 제목을 적어주세요.")
        private String title;

        @NotBlank(message = "내용을 적어주세요.")
        private String content;

        @NotBlank(message = "해시태그를 입력해주세요.")
        private String hashTag;
    }

    @Getter
    @Data
    @NoArgsConstructor
    public static class Patch {

        @NotBlank(message = "제목은 공백이 아니어야 합니다.")
        private String title;

        @NotBlank(message = "내용은 공백이 아니어야 합니다.")
        private String content;

        @NotBlank(message = "해시태그를 입력해주세요.")
        private String hashTag;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{

        private String title;

        private String content;

        private String hasgTag;
    }
}
