package com.finnex.finance_app.common.exceptions;

public class UnauthorizedException extends  RuntimeException{
    public UnauthorizedException(String message){
        super(message);
    }
}
