package ru.averkiev.greenchat_auth.services.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.averkiev.greenchat_auth.clients.impl.UserServiceClientImpl;
import ru.averkiev.greenchat_auth.exceptions.AuthException;
import ru.averkiev.greenchat_auth.models.AccessToken;
import ru.averkiev.greenchat_auth.models.RefreshToken;
import ru.averkiev.greenchat_auth.models.User;
import ru.averkiev.greenchat_auth.security.*;
import ru.averkiev.greenchat_auth.services.AuthService;

/**
 * Класс предоставляет функционал для аутентификации и авторизации пользователей.
 * @author mrGreenNV
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClientImpl userServiceClient;
    private final AccessTokenServiceImpl accessTokenService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Выполняет аутентификацию пользователя при входе в систему.
     * @param authRequest - запрос на аутентификацию пользователя.
     * @return - ответ на запрос аутентификации пользователя, содержащий access и refresh токены.
     * @throws AuthException - выбрасывается, если был передан невалидный пароль.
     */
    @Override
    public JwtResponse login(JwtRequest authRequest) {

        // Получение данных из микросервиса пользователей.
        final User user = userServiceClient.getUserByUsername(authRequest.getLogin());

        // Сравнение пароля, полученного из запроса аутентификации с паролем, полученным от микросервиса
        // пользователей.
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            JwtUser jwtUser = JwtUserFactory.created(user);

            // Генерация access токена с помощью JwtProvider.
            final String accessTokenStr = jwtProvider.generateAccessToken(jwtUser);
            // Создание объекта AccessToken.
            AccessToken accessToken = new AccessToken(
                    jwtUser.getId(),
                    accessTokenStr,
                    jwtProvider.getAccessClaims(accessTokenStr).getIssuedAt(),
                    jwtProvider.getAccessClaims(accessTokenStr).getExpiration()
            );
            // Сохранение access токена в базе данных.
            accessTokenService.save(accessToken);

            // Генерация access токена с помощью JwtProvider.
            final String refreshTokenStr = jwtProvider.generateRefreshToken(jwtUser);
            // Создание объекта AccessToken.
            RefreshToken refreshToken = new RefreshToken(
                    jwtUser.getId(),
                    refreshTokenStr,
                    jwtProvider.getAccessClaims(refreshTokenStr).getIssuedAt(),
                    jwtProvider.getAccessClaims(refreshTokenStr).getExpiration()
            );
            // Сохранение access токена в базе данных.
            refreshTokenService.save(refreshToken);

            return new JwtResponse(accessTokenStr, refreshTokenStr);
        } else {
            throw new AuthException("Неправильный пароль");
        }
    }

    /**
     * Получение нового access токена на основе переданного refresh токена.
     * @param refreshToken - refresh токен.
     * @return объект JwtResponse, содержащий новый access токен.
     */
    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();

            final User user = userServiceClient.getUserByUsername(username);
            final RefreshToken saveRefreshToken = refreshTokenService.findByUserId(user.getId()).orElse(null);

            if (saveRefreshToken != null && saveRefreshToken.getAccessToken().equals(refreshToken)) {
                JwtUser jwtUser = JwtUserFactory.created(user);
                // Генерация access токена с помощью JwtProvider.
                final String accessTokenStr = jwtProvider.generateAccessToken(jwtUser);
                // Создание объекта AccessToken.
                final AccessToken newAccessToken = new AccessToken(
                        jwtUser.getId(),
                        accessTokenStr,
                        jwtProvider.getAccessClaims(accessTokenStr).getIssuedAt(),
                        jwtProvider.getAccessClaims(accessTokenStr).getExpiration()
                );

                // Обновление access токена в базе данных.
                accessTokenService.update(jwtUser.getId(), newAccessToken);
                return new JwtResponse(accessTokenStr, null);
            }
        }
        return new JwtResponse(null, null);
    }

    /**
     * Обновление access и refresh токенов, на основе переданного refresh токена.
     * @param refreshToken - refresh токен.
     * @return - объект JwtResponse, содержащий новые access и refresh токены.
     * @throws AuthException выбрасывается, если передан недействительный JWT токен.
     */
    @Override
    public JwtResponse refresh(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String username = claims.getSubject();

            final User user = userServiceClient.getUserByUsername(username);
            final RefreshToken saveRefreshToken = refreshTokenService.findByUserId(user.getId()).orElse(null);

            if (saveRefreshToken != null && saveRefreshToken.getAccessToken().equals(refreshToken)) {
                JwtUser jwtUser = JwtUserFactory.created(user);

                // Генерация access токена с помощью JwtProvider.
                final String accessTokenStr = jwtProvider.generateAccessToken(jwtUser);
                // Создание объекта AccessToken.
                final AccessToken newAccessToken = new AccessToken(
                        jwtUser.getId(),
                        accessTokenStr,
                        jwtProvider.getAccessClaims(accessTokenStr).getIssuedAt(),
                        jwtProvider.getAccessClaims(accessTokenStr).getExpiration()
                );

                // Генерация refresh токена с помощью JwtProvider.
                final String refreshTokenStr = jwtProvider.generateRefreshToken(jwtUser);
                // Создание объекта RefreshToken.
                final RefreshToken newRefreshToken = new RefreshToken(
                        jwtUser.getId(),
                        refreshTokenStr,
                        jwtProvider.getRefreshClaims(refreshTokenStr).getIssuedAt(),
                        jwtProvider.getRefreshClaims(refreshTokenStr).getExpiration()
                );

                // Обновление access токена в базе данных.
                accessTokenService.update(jwtUser.getId(), newAccessToken);
                // Обновление refresh токена в базе данных.
                refreshTokenService.update(jwtUser.getId(), newRefreshToken);
            }
        }
        throw new AuthException("Неверный JWT токен");
    }

    /**
     * Получение информации об аутентификации пользователя.
     * @return JwtAuthentication, содержащий информацию об аутентификации пользователя.
     */
    @Override
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}