package com.aiplus.backend.users.exceptions;

public class ClientAccountNotFoundException extends RuntimeException {

    public ClientAccountNotFoundException(String msg) {
        super(msg);
    }

}
