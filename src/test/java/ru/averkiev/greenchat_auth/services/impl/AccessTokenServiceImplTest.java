package ru.averkiev.greenchat_auth.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.averkiev.greenchat_auth.models.AccessToken;
import ru.averkiev.greenchat_auth.repo.AccessTokenRepository;

import java.util.Optional;

/**
 * Тестовый класс для проверки функциональности AccessTokenServiceImpl.
 * Этот класс представляет собой сервис, реализующий функционал сохранения, обновления, удаления и поиска access
 * токенов с использованием базы данных.
 */
public class AccessTokenServiceImplTest {

    @Mock
    private AccessTokenRepository accessTokenRepository;

    @InjectMocks
    AccessTokenServiceImpl accessTokenServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет верную обработку результата сохранения токена в базе данных при успешном сохранении.
     */
    @Test
    public void save_ShouldReturnTrue_WhenAccessTokenSuccessfully() {
        AccessToken accessToken = new AccessToken();

        Mockito.when(accessTokenRepository.save(accessToken)).thenReturn(accessToken);

        boolean resultSave = accessTokenServiceImpl.save(accessToken);

        Assertions.assertTrue(resultSave);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(accessToken);
    }

    /**
     * Проверяет верную обработку результата сохранения токена в базе данных с ошибкой во время сохранения.
     */
    @Test
    public void save_ShouldReturnFalse_WhenAccessTokenNotSaved() {
        AccessToken accessToken = new AccessToken();

        Mockito.when(accessTokenRepository.save(accessToken)).thenThrow(new RuntimeException());

        boolean result = accessTokenServiceImpl.save(accessToken);

        Assertions.assertFalse(result);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(accessToken);
    }

    /**
     * Проверяет верную обработку результата обновления токена в базе данных при успешном обновлении.
     */
    @Test
    public void update_ShouldReturnTrue_WhenAccessTokenUpdatedSuccessfully() {
        int userId = 1;
        AccessToken accessToken = new AccessToken();
        accessToken.setId(1);

        AccessToken updateAccessToken = new AccessToken();

        Mockito.when(accessTokenRepository.findByUserId(userId)).thenReturn(Optional.of(accessToken));
        Mockito.when(accessTokenRepository.save(updateAccessToken)).thenReturn(updateAccessToken);

        boolean result = accessTokenServiceImpl.update(userId, updateAccessToken);

        Assertions.assertTrue(result);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(updateAccessToken);
    }

    /**
     * Проверяет верную обработку результата обновления токена в базе данных с ошибкой во время обновления.
     */
    @Test
    public void update_ShouldThrowException_WhenAccessTokenNotFound() {
        int userId = 1;
        AccessToken accessToken = new AccessToken();
        accessToken.setId(1);

        AccessToken updateAccessToken = new AccessToken();

        Mockito.when(accessTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        boolean result = accessTokenServiceImpl.update(userId, updateAccessToken);

        Assertions.assertFalse(result);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(accessTokenRepository, Mockito.times(0)).save(updateAccessToken);
    }

    /**
     * Проверяет верную обработку результата поиска access токена в базе данных по идентификатору пользователя,
     * в случае, когда найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void findByUserId_ShouldReturnAccessToken_WhenFound() {
        int userId = 1;
        AccessToken accessToken = new AccessToken();

        Mockito.when(accessTokenRepository.findByUserId(userId)).thenReturn(Optional.of(accessToken));

        Optional<AccessToken> result = accessTokenRepository.findByUserId(userId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(accessToken, result.get());
        Mockito.verify(accessTokenRepository, Mockito.times(1)).findByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата поиска access токена в базе данных по идентификатору пользователя,
     * в случае, когда не найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void findByUserId_ShouldReturnEmptyOptional_WhenNotFound() {
        int userId = 1;

        Mockito.when(accessTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<AccessToken> result = accessTokenRepository.findByUserId(userId);

        Assertions.assertFalse(result.isPresent());
        Mockito.verify(accessTokenRepository, Mockito.times(1)).findByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата удаления access токена из базы данных по идентификатору пользователя,
     * в случае, когда найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void delete_ShouldReturnTrue_WhenAccessTokenDeletedSuccessfully() {
        int userId = 1;

        Mockito.doNothing().when(accessTokenRepository).deleteByUserId(userId);

        boolean result = accessTokenServiceImpl.delete(userId);

        Assertions.assertTrue(result);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).deleteByUserId(userId);
    }

    /**
     * Проверяет верную обработку результата удаления access токена из базы данных по идентификатору пользователя,
     * в случае, когда не найден токен с заданным идентификатором пользователя.
     */
    @Test
    public void delete_ShouldReturnFalse_WhenAccessTokenNotDeleted() {
        int userId = 1;

        Mockito.doThrow(new RuntimeException()).when(accessTokenRepository).deleteByUserId(userId);

        boolean result = accessTokenServiceImpl.delete(userId);

        Assertions.assertFalse(result);
        Mockito.verify(accessTokenRepository, Mockito.times(1)).deleteByUserId(userId);
    }
}