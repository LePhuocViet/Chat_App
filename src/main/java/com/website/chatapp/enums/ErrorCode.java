package com.website.chatapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    //400 Bad request

    //401 Authentication
    TOKEN_INVALID(40101,"Token invalid",HttpStatus.UNAUTHORIZED),
    PASSWORD_IS_INCORRECT(40102,"Password is incorrect",HttpStatus.UNAUTHORIZED),
    //403 Forbidden

    //404 Not Found
    USER_NOT_FOUND(40401,"User not found",HttpStatus.NOT_FOUND),


    ;
    private Integer code;
    private String message;
    private HttpStatus httpStatus;
}
