package com.faturium.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    private final String title;
    private final String description;
    private final HttpStatus statusCode;

    public AppException(String title, String description, HttpStatus statusCode){
        super(description);

        this.title = title;
        this.description = description;
        this.statusCode = statusCode;
    }
}
