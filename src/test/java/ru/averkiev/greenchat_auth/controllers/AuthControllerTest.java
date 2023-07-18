package ru.averkiev.greenchat_auth.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.averkiev.greenchat_auth.models.JwtRequest;
import ru.averkiev.greenchat_auth.models.JwtRequestRefresh;
import ru.averkiev.greenchat_auth.models.JwtResponse;
import ru.averkiev.greenchat_auth.services.impl.AuthServiceImpl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки функциональности AuthController
 */
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private AuthController authController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Проверяет аутентификацию пользователя при входе в систему.
     * Ожидается успешный ответ с access и refresh токенами.
     */
    @Test
    public void testLogin() {
        // Создание тестовых данных.
        JwtRequest jwtRequest = new JwtRequest("test_user", encoder.encode("testPassword"));
        JwtResponse jwtResponse = new JwtResponse("testAccessToken", "testRefreshToken");

        when(authService.login(jwtRequest)).thenReturn(jwtResponse);

        // Вызов тестируемого метода.
        authController.login(jwtRequest);

        // Проверка результатов.
        verify(authService).login(jwtRequest);
    }

    /**
     * Проверяет получение нового access токена на основе переданного refresh токена.
     * Ожидается успешный ответ с access токеном и значением null вместо refresh токена.
     */
    @Test
    public void testGetNewAccessToken() {
        // Создание тестовых данных.
        JwtRequestRefresh jwtRequestRefresh = new JwtRequestRefresh();
        jwtRequestRefresh.setRefreshToken("test_refresh_token");
        JwtResponse jwtResponse = new JwtResponse("test_access_token", null);

        when(authService.getAccessToken(jwtRequestRefresh.getRefreshToken())).thenReturn(jwtResponse);

        // Вызов тестируемого метода.
        authController.getNewAccessToken(jwtRequestRefresh);

        // Проверка результатов.
        verify(authService).getAccessToken(jwtRequestRefresh.getRefreshToken());
    }

    /**
     * Проверяет обновление access и refresh токенов на основе переданного refresh токена.
     * Ожидается успешный ответ с access и refresh токенами.
     */
    @Test
    public void testGetNewRefreshToken() {
        // Создание тестовых данных.
        JwtRequestRefresh jwtRequestRefresh = new JwtRequestRefresh();
        jwtRequestRefresh.setRefreshToken("test_refresh_token");
        JwtResponse jwtResponse = new JwtResponse("test_access_token", "test_refresh_token");

        when(authService.refresh(jwtRequestRefresh.getRefreshToken())).thenReturn(jwtResponse);

        // Вызов проверяемого метода.
        authController.getNewRefreshToken(jwtRequestRefresh);

        // Проверка результатов.
        verify(authService).refresh(jwtRequestRefresh.getRefreshToken());
    }
}