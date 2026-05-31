package com.finnex.finance_app.common.exceptions;

public class ResourceNotFound extends  RuntimeException{
    public ResourceNotFound(String message) {
        super(message);
    }
}
