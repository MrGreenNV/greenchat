package ru.averkiev.greenchat_auth.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.averkiev.greenchat_auth.exceptions.TokenNotFoundException;
import ru.averkiev.greenchat_auth.models.RefreshToken;
import ru.averkiev.greenchat_auth.repo.RefreshTokenRepository;

import java.util.Optional;

/**
 * Тестовый класс для проверки функциональности RefreshTokenServiceImpl.
 * Этот класс представляет собой сервис, реализующий функционал сохранения, обновления, удаления и поиска refresh
 * токенов с использованием базы данных.
 */
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет верную обработку результата сохранения токена в базе данных при успешном сохранении.
     */
    @Test
    public void save_ShouldReturnTrue_WhenRefreshTokenSuccessfully() {
        RefreshToken refreshToken = new RefreshToken();

        Mockito.when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        boolean resultSave = refreshTokenServiceImpl.save(refreshToken);

        Assertions.assertTrue(resultSave);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(refreshToken);
    }

    /**
     * Проверяет верную обработку результата сохранения токена в базе данных с ошибкой во время сохранения.
     */
    @Test
    public void save_ShouldReturnFalse_WhenRefreshTokenNotSaved() {
        RefreshToken refreshToken = new RefreshToken();

        Mockito.when(refreshTokenRepository.save(refreshToken)).thenThrow(new RuntimeException());

        boolean result = refreshTokenServiceImpl.save(refreshToken);

        Assertions.assertFalse(result);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(refreshToken);
    }

    /**
     * Проверяет верную обработку результата обновления токена в базе данных при успешном обновлении.
     */
    @Test
    public void update_ShouldReturnTrue_WhenRefreshTokenUpdatedSuccessfully() {
        int userId = 1;
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1);

        RefreshToken updateRefreshToken = new RefreshToken();

        Mockito.when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(refreshToken));
        Mockito.when(refreshTokenRepository.save(updateRefreshToken)).thenReturn(updateRefreshToken);

        boolean result = refreshTokenServiceImpl.update(userId, updateRefreshToken);

        Assertions.assertTrue(result);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(updateRefreshToken);
    }

    /**
     * Проверяет верную обработку результата обновления токена в базе данных с ошибкой во время обновления.
     */
    @Test
    public void update_ShouldThrowException_WhenRefreshTokenNotFound() {
        int userId = 1;
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1);

        RefreshToken updateRefreshToken = new RefreshToken();

        Mockito.when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        TokenNotFoundException exception = Assertions.assertThrows(TokenNotFoundException.class, () -> refreshTokenServiceImpl.update(userId, updateRefreshToken));
        Assertions.assertEquals("Токен не найден", exception.getMessage());
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(refreshTokenRepository, Mockito.times(0)).save(updateRefreshToken);
    }

    /**
     * Проверяет верную обработку результата поиска access токена в базе данных по идентификатору пользователя,
     * в случае, когда найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void findByUserId_ShouldReturnRefreshToken_WhenFound() {
        int userId = 1;
        RefreshToken refreshToken = new RefreshToken();

        Mockito.when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenRepository.findByUserId(userId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(refreshToken, result.get());
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата поиска access токена в базе данных по идентификатору пользователя,
     * в случае, когда не найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void findByUserId_ShouldReturnEmptyOptional_WhenNotFound() {
        int userId = 1;

        Mockito.when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        TokenNotFoundException exception = Assertions.assertThrows(TokenNotFoundException.class, () -> refreshTokenServiceImpl.findByUserId(userId));
        Assertions.assertEquals("Токен не найден", exception.getMessage());
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).findByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата удаления access токена из базы данных по идентификатору пользователя,
     * в случае, когда найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void delete_ShouldReturnTrue_WhenRefreshTokenDeletedSuccessfully() {
        int userId = 1;

        Mockito.doNothing().when(refreshTokenRepository).deleteByUserId(userId);

        boolean result = refreshTokenServiceImpl.delete(userId);

        Assertions.assertTrue(result);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).deleteByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата удаления access токена из базы данных по идентификатору пользователя,
     * в случае, когда не найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void delete_ShouldReturnFalse_WhenAccessTokenNotDeleted() {
        int userId = 1;

        Mockito.doThrow(new RuntimeException()).when(refreshTokenRepository).deleteByUserId(userId);

        boolean result = refreshTokenServiceImpl.delete(userId);

        Assertions.assertFalse(result);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).deleteByUserId(userId);
    }
}
