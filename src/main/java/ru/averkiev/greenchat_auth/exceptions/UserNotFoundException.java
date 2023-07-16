package ru.averkiev.greenchat_auth.exceptions;

/**
 * Класс представляет собой исключение, которое возникает в случае, когда пользователь не найден по идентификатору.
 * @author mrGreenNV
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg) {super(msg);}
}
