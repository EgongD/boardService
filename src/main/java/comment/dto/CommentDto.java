package comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class CommentDto {

    @Getter
    @NoArgsConstructor
    public static class Post{

        private Long memberId;

        private Long boardId;

        @NotBlank(message = "내용을 적어주세요.")
        private String content;
    }

    @Getter
    @Data
    @NoArgsConstructor
    public static class Patch{

        private Long memberId;

        private Long boardId;

        @NotBlank(message = "내용을 적어주세요.")
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{

        private String content;
    }
}
