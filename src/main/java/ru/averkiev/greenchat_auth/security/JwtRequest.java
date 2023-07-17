package ru.averkiev.greenchat_auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс представляет запрос на аутентификацию с использованием JWT.
 * Этот класс используется для передачи данных аутентификации (логина и пароля) от клиента к микросервису
 * аутентификации и авторизации.
 * @author mrGreenNV
 */
@Getter
@AllArgsConstructor
public class JwtRequest {
    private String login;
    private String password;
}