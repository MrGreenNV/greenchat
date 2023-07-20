package ru.averkiev.greenchat_auth.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс представляет собой модель для передачи в запросе JWT refresh токена.
 * @author mrGreenNV
 */
@Getter
@Setter
public class JwtRequestRefresh {
    private String refreshToken;
}