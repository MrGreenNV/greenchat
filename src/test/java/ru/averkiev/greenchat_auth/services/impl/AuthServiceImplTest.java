package ru.averkiev.greenchat_auth.services.impl;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.averkiev.greenchat_auth.exceptions.AuthException;
import ru.averkiev.greenchat_auth.models.*;
import ru.averkiev.greenchat_auth.security.*;
import ru.averkiev.greenchat_auth.services.JwtUserDetailsService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности AuthServiceImpl.
 * Этот класс представляет собой функционал для аутентификации и авторизации пользователей.
 */
public class AuthServiceImplTest {

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private AccessTokenServiceImpl accessTokenService;
    @Mock
    private RefreshTokenServiceImpl refreshTokenService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    private final String username = "test_User";
    private final String password = new BCryptPasswordEncoder().encode("testPassword");
    private final String firstname = "test";
    private final String lastname = "User";
    private final String email = "test@gmail.com";
    private final String status = "ACTIVE";
    private final List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

    private final User user = new User(
            0,
            username,
            password,
            firstname,
            lastname,
            email,
            status,
            roles
    );

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(jwtUserDetailsService, accessTokenService, refreshTokenService, jwtProvider, passwordEncoder);
    }

    /**
     * Проверяет формирование ответа на запрос аутентификации при валидных данных от пользователя.
     */
    @Test
    public void login_ValidCredentials_ReturnJwtResponse() {
        // Создание тестовых данных.
        JwtRequest authRequest = new JwtRequest(username, password);
        JwtUser jwtUser = JwtUserFactory.created(user);

        when(jwtUserDetailsService.loadUserByUsername(username)).thenReturn(jwtUser);
        when(passwordEncoder.matches(password, jwtUser.getPassword())).thenReturn(true);

        String accessTokenStr = "access_token";
        String refreshTokenStr = "refresh_token";

        AccessToken accessToken = new AccessToken(0, accessTokenStr, new Date(), new Date());
        RefreshToken refreshToken = new RefreshToken(0, refreshTokenStr, new Date(), new Date());

        when(jwtProvider.generateAccessToken(jwtUser)).thenReturn(accessTokenStr);
        when(jwtProvider.generateRefreshToken(jwtUser)).thenReturn(refreshTokenStr);
        when(jwtProvider.getAccessClaims(accessTokenStr)).thenReturn(mock(Claims.class));
        when(jwtProvider.getRefreshClaims(refreshTokenStr)).thenReturn(mock(Claims.class));

        when(accessTokenService.save(accessToken)).thenReturn(true);
        when(refreshTokenService.save(refreshToken)).thenReturn(true);

        // Вызов тестируемого метода.
        JwtResponse jwtResponse = authService.login(authRequest);

        // Проверка результата.
        Assertions.assertNotNull(jwtResponse);
        assertEquals(accessTokenStr, jwtResponse.getAccessToken());
        assertEquals(refreshTokenStr, jwtResponse.getRefreshToken());

        verify(jwtUserDetailsService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, user.getPassword());
        verify(jwtProvider, times(1)).generateAccessToken(jwtUser);
        verify(jwtProvider, times(1)).generateRefreshToken(jwtUser);
    }

    /**
     * Проверяет выброс исключения на запрос аутентификации при невалидных данных от пользователя.
     */
    @Test
    public void login_InvalidCredentials_ThrowsAuthException() {
        // Создание тестовых данных.
        JwtRequest authRequest = new JwtRequest(username, password);
        JwtUser jwtUser = JwtUserFactory.created(user);

        when(jwtUserDetailsService.loadUserByUsername(username)).thenReturn(jwtUser);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Вызов проверяемого метода.
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(authRequest));

        // Проверка результатов.
        assertEquals("Неправильный пароль", exception.getMessage());

        verify(jwtUserDetailsService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, user.getPassword());
    }

    /**
     * Проверяет формирование ответа на запрос обновления access токена при валидном refresh токена.
     */
    @Test
    public void getAccessToken_ValidRefreshToken_ReturnsJwtResponseWithNewAccessToken() {
        // Создание тестовых данных.
        String refreshTokenStr = "refresh_token";
        Claims claims = mock(Claims.class);

        JwtUser jwtUser = JwtUserFactory.created(user);
        RefreshToken saveRefreshToken = new RefreshToken(
                0,
                refreshTokenStr,
                new Date(),
                new Date()
        );

        when(jwtProvider.validateRefreshToken(refreshTokenStr)).thenReturn(true);
        when(jwtProvider.getRefreshClaims(refreshTokenStr)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(jwtUserDetailsService.loadUserByUsername(username)).thenReturn(jwtUser);
        when(refreshTokenService.findByUserId(user.getId())).thenReturn(Optional.of(saveRefreshToken));

        String accessTokenStr = "access_token";
        AccessToken newAccessToken = new AccessToken(
                0,
                accessTokenStr,
                new Date(),
                new Date()
        );

        when(jwtProvider.generateAccessToken(jwtUser)).thenReturn(accessTokenStr);
        when(jwtProvider.getAccessClaims(accessTokenStr)).thenReturn(claims);
        when(accessTokenService.update(jwtUser.getId(), newAccessToken)).thenReturn(true);

        // Вызов проверяемого метода.
        JwtResponse jwtResponse = authService.getAccessToken(refreshTokenStr);

        // Проверка результатов.
        assertNotNull(jwtResponse);
        assertEquals(accessTokenStr, jwtResponse.getAccessToken());
        assertNull(jwtResponse.getRefreshToken());

        verify(jwtProvider, times(1)).validateRefreshToken(refreshTokenStr);
        verify(jwtProvider, times(1)).getRefreshClaims(refreshTokenStr);
        verify(jwtProvider, times(1)).generateAccessToken(jwtUser);
        verify(jwtProvider, times(2)).getAccessClaims(accessTokenStr);
        verify(claims, times(1)).getSubject();
        verify(jwtUserDetailsService, times(1)).loadUserByUsername(username);
        verify(refreshTokenService, times(1)).findByUserId(jwtUser.getId());
    }

    /**
     * Проверяет формирование ответа на запрос обновления access токена при невалидном refresh токена.
     */
    @Test
    public void getAccessTokenInvalidRefreshToken_ReturnsJwtResponseWithNullValues() {
        // Создание тестовых данных
        String refreshTokenStr = "invalid_refresh_token";

        when(jwtProvider.validateRefreshToken(refreshTokenStr)).thenReturn(false);

        // Вызов тестируемого метода.
        JwtResponse jwtResponse = authService.getAccessToken(refreshTokenStr);

        // Проверка результатов.
        assertNotNull(jwtResponse);
        assertNull(jwtResponse.getAccessToken());
        assertNull(jwtResponse.getRefreshToken());

        verify(jwtProvider, times(1)).validateRefreshToken(refreshTokenStr);
        verify(jwtProvider, never()).getRefreshClaims(refreshTokenStr);
        verify(jwtProvider, never()).getAccessClaims(anyString());
        verify(jwtUserDetailsService, never()).loadUserByUsername(anyString());
    }

    /**
     * Проверяет формирование ответа на запрос обновления access и refresh токенов при валидном refresh токена.
     */
    @Test
    public void refresh_ValidRefreshToken_ReturnsJwtResponseWithAccessTokenAndRefreshToken() {
        // Создание тестовых данных.
        String refreshTokenStr = "refresh_token";
        RefreshToken refreshToken = new RefreshToken(
                0,
                refreshTokenStr,
                new Date(),
                new Date()
        );
        Claims claims = mock(Claims.class);

        when(jwtProvider.validateRefreshToken(refreshTokenStr)).thenReturn(true);
        when(jwtProvider.getRefreshClaims(refreshTokenStr)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);

        JwtUser jwtUser = JwtUserFactory.created(user);

        when(jwtUserDetailsService.loadUserByUsername(username)).thenReturn(jwtUser);

        String newAccessTokenStr = "new_accessToken";
        AccessToken newAccessToken = new AccessToken(
                0,
                newAccessTokenStr,
                new Date(),
                new Date()
        );

        String newRefreshTokenStr = "new_refresh_token";
        RefreshToken newRefreshToken = new RefreshToken(
                0,
                newRefreshTokenStr,
                new Date(),
                new Date()
        );

        when(jwtProvider.getRefreshClaims(newRefreshTokenStr)).thenReturn(mock(Claims.class));
        when(jwtProvider.generateRefreshToken(jwtUser)).thenReturn(newRefreshTokenStr);
        when(jwtProvider.getAccessClaims(newAccessTokenStr)).thenReturn(mock(Claims.class));
        when(jwtProvider.generateAccessToken(jwtUser)).thenReturn(newAccessTokenStr);
        when(accessTokenService.update(0, newAccessToken)).thenReturn(true);
        when(refreshTokenService.update(0, newRefreshToken)).thenReturn(true);
        when(refreshTokenService.findByUserId(jwtUser.getId())).thenReturn(Optional.of(refreshToken));

        // Вызов тестируемого метода.
        JwtResponse jwtResponse = authService.refresh(refreshTokenStr);

        // Проверка результатов.
        assertNotNull(jwtResponse);
        assertEquals(newAccessTokenStr, jwtResponse.getAccessToken());
        assertEquals(newRefreshTokenStr, jwtResponse.getRefreshToken());

        verify(jwtProvider, times(1)).validateRefreshToken(refreshTokenStr);
        verify(jwtProvider, times(1)).getRefreshClaims(refreshTokenStr);
        verify(claims, times(1)).getSubject();
        verify(jwtUserDetailsService, times(1)).loadUserByUsername(username);
        verify(jwtProvider, times(1)).generateAccessToken(jwtUser);
        verify(jwtProvider, times(1)).generateRefreshToken(jwtUser);
    }

    /**
     * Проверяет формирование ответа на запрос обновления access и refresh токенов при невалидном refresh токена.
     */
    @Test
    public void refresh_InvalidRefreshToken_ThrowsAuthException() {
        // Создание тестовых данных.
        String refreshTokenStr = "invalid_refresh_token";

        when(jwtProvider.validateRefreshToken(refreshTokenStr)).thenReturn(false);

        // Вызов тестируемого метода.
        AuthException exception = assertThrows(AuthException.class, () ->authService.refresh(refreshTokenStr));

        // Проверка результатов.
        assertEquals("Неверный JWT токен", exception.getMessage());
    }

    /**
     * Проверяет выдачу аутентификации из контекста при её наличии.
     */
    @Test
    public void getAuthInfo_AuthenticationNotNull_ReturnsJwtAuthentication() {
        // Создание тестовых данных.
        JwtAuthentication authentication = mock(JwtAuthentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Вызов тестируемого метода.
        JwtAuthentication result = authService.getAuthInfo();

        // Проверка результатов.
        assertNotNull(result);
        assertEquals(authentication, result);

        SecurityContextHolder.clearContext();
    }

    /**
     * Проверяет выдачу аутентификации из контекста при её отсутствии.
     */
    @Test
    public void getAuthInfo_AuthenticationNull_ReturnsNull() {
        // Создание тестовых данных.
        SecurityContextHolder.getContext().setAuthentication(null);

        // Вызов тестируемого метода.
        JwtAuthentication result = authService.getAuthInfo();

        // Проверка результатов.
        assertNull(result);

        SecurityContextHolder.clearContext();
    }
}
