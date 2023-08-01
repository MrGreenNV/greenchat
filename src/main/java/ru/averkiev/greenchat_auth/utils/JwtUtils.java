package ru.averkiev.greenchat_auth.utils;

import io.jsonwebtoken.Claims;
import lombok.NoArgsConstructor;
import ru.averkiev.greenchat_auth.security.JwtAuthentication;

/**
 * Класс JwtUtils предоставляет утилитарные методы для работы с JSON Web Token (JWT).
 * В данном классе реализован метод generate, который преобразует объект Claims (представление утверждений JWT) в
 * объект JwtAuthentication.
 * @author mrGreenNV
 */
@NoArgsConstructor
public final class JwtUtils {

    /**
     * Метод generate принимает объект Claims, который содержит утверждения из JWT, и возвращает объект
     * JwtAuthentication.
     *
     * @param claims объект Claims, содержащий утверждения из JSON Web Token (JWT).
     * @return объект JwtAuthentication, содержащий информацию о пользователе из JWT.
     */
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
//        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstname(claims.get("firstname", String.class));
        jwtInfoToken.setLogin(claims.getSubject());
        return jwtInfoToken;
    }
}
