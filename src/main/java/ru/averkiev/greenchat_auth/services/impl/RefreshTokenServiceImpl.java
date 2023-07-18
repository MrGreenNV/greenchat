package ru.averkiev.greenchat_auth.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.averkiev.greenchat_auth.exceptions.TokenNotFoundException;
import ru.averkiev.greenchat_auth.exceptions.UserNotFoundException;
import ru.averkiev.greenchat_auth.models.RefreshToken;
import ru.averkiev.greenchat_auth.repo.RefreshTokenRepository;
import ru.averkiev.greenchat_auth.services.RefreshTokenService;

import java.util.Optional;

/**
 * Класс реализует функционал взаимодействия refresh токена с базой данных (сохранение, обновление, удаление и
 * поиск по идентификатору пользователя, к которому относится токен).
 * @author mrGreenNV
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Сохраняет refresh токен в базу данных.
     * @param refreshToken токен, который необходимо сохранить в базе данных.
     * @return возвращает результат сохранения.
     */
    @Override
    public boolean save(RefreshToken refreshToken) {
        try {
            refreshTokenRepository.save(refreshToken);
            log.info("IN save - refresh токен с id: {} успешно сохранён", refreshToken.getId());
            return true;
        } catch (Exception ex) {
            log.error("IN save - refresh токен с id: {} сохранить не удалось", refreshToken.getId(), ex);
        }
        return false;
    }

    /**
     * Обновляет refresh токен в базе данных.
     * @param userId - идентификатор пользователя, токен которого необходимо обновить.
     * @param updateRefreshToken - обновленный токен.
     * @return - возвращает результат обновления.
     * @exception UserNotFoundException - исключение выбрасывается, если токен не найден в базе данных.
     */
    @Override
    public boolean update(int userId, RefreshToken updateRefreshToken) {
        RefreshToken refreshToken = findByUserId(userId).orElse(null);
        try {
            if (refreshToken == null) {
                throw new UserNotFoundException("Токен принадлежащий пользователю с id: " + userId + " не найден");
            }
            updateRefreshToken.setId(refreshToken.getId());
            refreshTokenRepository.save(updateRefreshToken);
            log.info("IN update - refresh токен пользователя с userId: {} успешно обновлён", userId);
            return true;
        } catch (UserNotFoundException tknEx) {
            log.error("IN update - refresh токен пользователя с userId: {} не был обновлен", userId, tknEx);
        }
        return false;
    }

    /**
     * Выполняет поиск refresh токена в базе данных по идентификатору пользователя.
     * @param userId - идентификатор пользователя, которому соответствует искомый refresh токен.
     * @return - Optional с результатами поиска.
     */
    @Override
    public Optional<RefreshToken> findByUserId(int userId) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(userId);
        if (refreshToken.isEmpty()) {
            throw new TokenNotFoundException("Токен не найден");
        }
        log.info("IN findByUserId - поиск завершён успешно.");
        return refreshToken;
    }

    /**
     * Удаляет из базы данных refresh токен по идентификатору пользователя, которому он соответствует.
     * @param userId - идентификатор пользователя, токен которого необходимо удалить.
     * @return - возвращает результат удаления.
     */
    @Override
    public boolean delete(int userId) {
        try {
            refreshTokenRepository.deleteByUserId(userId);
            log.info("IN delete - refresh токен пользователя с userId: {} успешно удалён", userId);
            return true;
        } catch (Exception Ex) {
            log.error("IN delete - refresh токен пользователя с userId: {} не был удалён", userId, Ex);
        }
        return false;
    }
}
