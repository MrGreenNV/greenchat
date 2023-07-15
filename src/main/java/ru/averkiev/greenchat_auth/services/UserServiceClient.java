package ru.averkiev.greenchat_auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.averkiev.greenchat_auth.models.User;

/**
 * Класс представляет собой компонент микросервиса, отвечающий за взаимодействие с API стороннего микросервиса
 * для получения данных о пользователе. Он предоставляет методы для выполнения запросов к API и получения
 * информации о пользователе на остове его идентификатора.
 * @author mrGreenNV
 */
@Component
public class UserServiceClient {
    /**
     * HTTP-клиент, используемый для выполнения запросов к API стороннего микросервиса. Зависимость должна быть
     * внедрена или передана в конструктор UserServiceClient.
     */
    private final RestTemplate restTemplate;
    @Value("${user_management.url}")
    String apiUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Метод выполняет GET-запрос к API стороннего микросервиса для получения информации о пользователе по его имени.
     * @param username - имя пользователя, для которого требуется получить информацию.
     * @return - объект User, содержащий информацию о пользователе.
     */
    public User getUserByUsername(String username) {
        ResponseEntity<User> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                User.class,
                username
        );
        return responseEntity.getBody();
    }
}