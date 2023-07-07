package global.exception;

import lombok.Getter;

public enum ExceptionCode {

    USER_EXIST(409, "사용중인 이메일 입니다."),

    USER_NOT_FOUND(404, "사용자가 없습니다."),

    USERNAME_EXIST(409, "사용중인 닉네임 입니다."),

    ACCESS_TOKEN_NOT_FOUND(404, "AccessToken not found"),

    REFRESH_TOKEN_NOT_FOUND(404, "RefreshToken이 없습니다."),

    BOARD_NOT_FOUND(404, "글이 없습니다."),

    COMMENT_NOT_FOUND(404, "댓글이 없습니다."),

    UNAUTHORIZED(409, "직성자가 동일하지 않습니다."),

    DIFFERENT_REFRESHTOKEN(409, "RefreshTokens are different");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
