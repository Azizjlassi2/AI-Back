package com.aiplus.backend.users.exceptions;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
