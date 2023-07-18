package ru.averkiev.greenchat_auth.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.averkiev.greenchat_auth.exceptions.UserNotFoundException;
import ru.averkiev.greenchat_auth.models.AccessToken;
import ru.averkiev.greenchat_auth.repo.AccessTokenRepository;
import ru.averkiev.greenchat_auth.services.AccessTokenService;

import java.util.Optional;

/**
 * Класс реализует функционал взаимодействия access токена с базой данных (сохранение, обновление, удаление и
 * поиск по идентификатору пользователя, к которому относится токен).
 * @author mrGreenNV
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    /**
     * Сохраняет access токен в базу данных.
     * @param accessToken токен, который необходимо сохранить в базе данных.
     * @return возвращает результат сохранения.
     */
    @Override
    public boolean save(AccessToken accessToken) {
        try {
            accessTokenRepository.save(accessToken);
            log.info("IN save - access токен с id: {} успешно сохранён", accessToken.getId());
            return true;
        } catch (Exception ex) {
            log.error("IN save - access токен с id: {} сохранить не удалось", accessToken.getId(), ex);
        }
        return false;
    }

    /**
     * Обновляет access токен в базе данных.
     * @param userId - идентификатор пользователя, токен которого необходимо обновить.
     * @param updateAccessToken - обновленный токен.
     * @return - возвращает результат обновления.
     * @exception UserNotFoundException - исключение выбрасывается, если токен не найден в базе данных.
     */
    @Override
    public boolean update(int userId, AccessToken updateAccessToken) {
        AccessToken accessToken = findByUserId(userId).orElse(null);
        try {
            if (accessToken == null) {
                throw new UserNotFoundException("Токен принадлежащий пользователю с id: " + userId + " не найден");
            }
            updateAccessToken.setId(accessToken.getId());
            accessTokenRepository.save(updateAccessToken);
            log.info("IN update - access токен пользователя с userId: {} успешно обновлён", userId);
            return true;
        } catch (UserNotFoundException tknEx) {
            log.error("IN update - access токен пользователя с userId: {} не был обновлен", userId, tknEx);
        }
        return false;
    }

    /**
     * Выполняет поиск access токена в базе данных по идентификатору пользователя.
     * @param userId - идентификатор пользователя, которому соответствует искомый access токен.
     * @return - Optional с результатами поиска.
     */
    @Override
    public Optional<AccessToken> findByUserId(int userId) {
        Optional<AccessToken> accessToken = accessTokenRepository.findByUserId(userId);
        log.info("IN findByUserId - поиск завершён успешно.");
        return accessToken;
    }

    /**
     * Удаляет из базы данных access токен по идентификатору пользователя, которому он соответствует.
     * @param userId - идентификатор пользователя, токен которого необходимо удалить.
     * @return - возвращает результат удаления.
     */
    @Override
    public boolean delete(int userId) {
        try {
            accessTokenRepository.deleteByUserId(userId);
            log.info("IN delete - access токен пользователя с userId: {} успешно удалён", userId);
            return true;
        } catch (Exception Ex) {
            log.error("IN delete - access токен пользователя с userId: {} не был удалён", userId, Ex);
        }
        return false;
    }
}