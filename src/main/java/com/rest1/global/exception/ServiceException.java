package com.rest1.global.exception;


public class ServiceException extends RuntimeException {
    private String msg;
    private String resultCode;

    public ServiceException(String resultCode,String message) {
        super("%s : %s".formatted(resultCode,message));
        this.resultCode = resultCode;
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }
    public String getResultCode() {
        return resultCode;
    }

}
