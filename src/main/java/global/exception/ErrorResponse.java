package global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;

    private String error;

    private String message;

    public static ErrorResponse of(HttpStatus httpStatus){

        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), null);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message){

        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }
}
