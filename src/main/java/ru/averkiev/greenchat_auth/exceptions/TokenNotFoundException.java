package ru.averkiev.greenchat_auth.exceptions;/**
*
* @author mrGreenNV
*/public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String msg) {
        super(msg);
    }
}
