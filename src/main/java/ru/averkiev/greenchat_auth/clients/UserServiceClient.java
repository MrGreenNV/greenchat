package ru.averkiev.greenchat_auth.clients;

import ru.averkiev.greenchat_auth.models.User;

/**
 * Интерфейс для взаимодействия с микросервисом пользователей.
 * @author mrGreenNV
 */
public interface UserServiceClient {
    User getUserByUsername(String name);
}
