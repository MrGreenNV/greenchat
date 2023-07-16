package ru.averkiev.greenchat_auth.services;

import ru.averkiev.greenchat_auth.models.RefreshToken;

import java.util.Optional;

/**
 * Интерфейс представляет собой функционал для сохранения, обновления, поиска и удаления access токена.
 * @author mrGreenNV
 */
public interface RefreshTokenService {
    boolean save(RefreshToken refreshToken);
    boolean update(int userId, RefreshToken updateRefresh);
    Optional<RefreshToken> findByUserId(int userId);
    boolean delete(int userId);
}
