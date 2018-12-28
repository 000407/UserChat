package com.kaze2.userchat.exception;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message){
        super(message);
    }
}
