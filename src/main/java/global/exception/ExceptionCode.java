package global.exception;

import lombok.Getter;

public enum ExceptionCode {

    MEMBER_EXIST(409, "사용중인 이메일 입니다."),

    MEMBER_NOT_FOUND(404, "사용자가 없습니다."),

    NICKNAME_EXIST(409, "사용중인 닉네임 입니다."),

    ACCESS_TOKEN_NOT_FOUND(404, "AccessToken not found"),

    REFRESH_TOKEN_NOT_FOUND(404, "RefreshToken이 없습니다."),

    BOARD_NOT_FOUND(404, "글이 없습니다."),

    DIFFERENT_REFRESHTOKEN(409, "RefreshTokens are different");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message){
        this.status = code;
        this.message = message;
    }
}
