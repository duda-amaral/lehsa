package com.gerenciadorlehsa.exceptions.lancaveis;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataException extends RuntimeException{
    public DataException(String message) {
        super(message);
    }
}
