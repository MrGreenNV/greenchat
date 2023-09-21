package ru.averkiev.greenchat_auth.exceptions;

/**
 * Класс представляет собой исключение, которое возникает в случае, когда пароль из JwtRequest не совпадает с
 * паролем, сохраненным в базе данных.
 * @author mrGreenNV
 */
public class AuthException extends RuntimeException {

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
