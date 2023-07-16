package ru.averkiev.greenchat_auth.services;

import ru.averkiev.greenchat_auth.models.AccessToken;

import java.util.Optional;

/**
 * Интерфейс представляет собой функционал для сохранения, обновления, поиска и удаления access токена.
 * @author mrGreenNV
 */
public interface AccessTokenService {
    boolean save(AccessToken accessToken);
    boolean update(int id, AccessToken updateAccessToken);
    Optional<AccessToken> findByUserId(int userId);
    boolean delete(int id);
}
