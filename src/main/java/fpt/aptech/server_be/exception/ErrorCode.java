package fpt.aptech.server_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001,"Invalid message key"),

    //nhung exception k mong muon xay ra se vo day
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error"),
    USER_EXISTED(1001, "User already exists"),
    USERNAME_INVALID(1003,"Username must be at least 4 characters"),
    INVALID_PASSWORD(1004,"Password must be at least 4 characters"),
    USER_NOT_EXISTED(1005,"User not existed"),
    //chua login
    UNAUTHENTICATED(1006,"Unauthenticated"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
