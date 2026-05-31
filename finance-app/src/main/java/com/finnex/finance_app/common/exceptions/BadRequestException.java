package com.finnex.finance_app.common.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String meesage){
        super(meesage);
    }
}
