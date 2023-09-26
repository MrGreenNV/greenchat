package ru.averkiev.greenchat_auth.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.averkiev.greenchat_auth.models.User;
import ru.averkiev.greenchat_auth.clients.impl.UserServiceClientImpl;
import ru.averkiev.greenchat_auth.models.JwtUser;
import ru.averkiev.greenchat_auth.models.JwtUserFactory;

/**
 * Класс предоставляет сервис для загрузки пользователей по имени пользователя, реализуя интерфейс UserDetailsService.
 * Этот класс используется для аутентификации и авторизации пользователей в greenchat с использованием JWT.
 * @author mrGreenNV
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    /**
     * UserServiceClient используется для получения данных о пользователе посредством API-вызова к стороннему сервису.
     */
    private final UserServiceClientImpl userServiceClientImpl;

    /**
     * Загружает и возвращает объект UserDetails для пользователя с заданным именем. Использует UserServiceClient для
     * получения информации о пользователе.
     * @param login - имя пользователя, для которого необходимо загрузить и вернуть объект UserDetails.
     * @return - возвращает объект JwtUser с использованием JwtUserFactory.
     * @throws UsernameNotFoundException - выбрасывается в случае, когда по имени не удалось найти пользователя.
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userServiceClientImpl.getUserByLogin(login);

        if (user == null) {
            throw new  UsernameNotFoundException("Пользователь с логином:" + login + " не найден");
        }

        JwtUser jwtUser = JwtUserFactory.created(user);
        log.info("IN loadUserByUsername - пользователь с логином: {} успешно загружен", login);

        return jwtUser;
    }
}