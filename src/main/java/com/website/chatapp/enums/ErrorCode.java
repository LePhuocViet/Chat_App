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

    //403 Forbidden

    //404 Not Found


    ;
    private Integer code;
    private String message;
    private HttpStatus httpStatus;
}
