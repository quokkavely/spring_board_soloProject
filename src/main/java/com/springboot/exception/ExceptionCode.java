package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"Member not found"),
    MEMBER_ALREADY_EXIST(409,"Member already exist"),
    POST_NOT_FOUND(404,"Post not found"),
    POST_ALREADY_EXIST(409,"Post already exist"),
    ONLY_ADMIN_CAN_WRITE(403, "Only administrators can write answer");
    @Getter
    private int statusCode;
    @Getter
    private String message;

    ExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
