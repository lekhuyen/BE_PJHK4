package fpt.aptech.server_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //sai key trong entity (valid)
    INVALID_KEY(1001,"Invalid message key", HttpStatus.BAD_REQUEST),

    //nhung exception k mong muon xay ra se vo day
//    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already exists",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 4 characters",HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004,"Password must be at least 4 characters",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User not existed",HttpStatus.NOT_FOUND),
    //chua login
    UNAUTHENTICATED(1006,"Unauthenticated",HttpStatus.UNAUTHORIZED),
    //k co quyen truy cap
    UNAUTHORIZED(1007,"You do not have permission",HttpStatus.FORBIDDEN),

    //category name exists
    CATEGORY_EXISTS(1008,"Category name already exists",HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_EXISTS(1009,"Product do not existed",HttpStatus.BAD_REQUEST),
    USER_INACTIVE(1010,"You had been locked",HttpStatus.BAD_REQUEST),

    PRICE_HIGHER_CURRENT_PRICE(1011,"Your bid must be higher than your previously bid",HttpStatus.BAD_REQUEST),

    SAME_CITIZEN(1012,"This identification number has been registered, please use another citizen identification number!",HttpStatus.BAD_REQUEST),
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
