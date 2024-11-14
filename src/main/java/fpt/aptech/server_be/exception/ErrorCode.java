package fpt.aptech.server_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //sai key trong entity (valid)
    INVALID_KEY(1001,"Invalid message key", HttpStatus.BAD_REQUEST),

    //nhung exception k mong muon xay ra se vo day
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already exists",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 4 characters",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"Password must be at least 4 characters",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User not existed",HttpStatus.NOT_FOUND),
    //chua login
    UNAUTHENTICATED(1006,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    //k co quyen truy cap
    UNAUTHORIZED(1007,"You do not have permission",HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}