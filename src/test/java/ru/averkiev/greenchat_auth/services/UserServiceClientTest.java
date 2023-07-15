package ru.averkiev.greenchat_auth.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.averkiev.greenchat_auth.models.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности класса UserServiceClient.
 * Этот класс выполняет тестирование взаимодействия с API стороннего микросервиса для получения данных о пользователе.
 */

public class UserServiceClientTest {

    @Value("${user_management.url}")
    String apiUrl;

    /**
     * Проверяет, что метод getByUsername возвращает ожидаемого пользователя по его имени.
     */
    @Test
    void getUserByUsername_ShouldReturnUser() {
        // Создание тестовых данных.

        String username = "Bob_Smith";
        User expectedUser = new User(
                0,
                username,
                "123456",
                "Bob",
                "Smith",
                "bob@gmail.com",
                "ACTIVE",
                Set.of("ROLE_USER")
        );

        // Создание заглушки RestTemplate.
        RestTemplate restTemplate = mock(RestTemplate.class);

        // Создание имитации ResponseEntity, который возвращает ожидаемого пользователя.
        ResponseEntity<User> responseEntity = new ResponseEntity<>(expectedUser, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                eq(null),
                eq(User.class),
                eq(username)
        )).thenReturn(responseEntity);

        // Создание экземпляра UserServiceClient с использованием заглушки RestTemplate.
        UserServiceClient userServiceClient = new UserServiceClient(restTemplate);

        // Выполнение метода getUserByUsername.
        User actualUser = userServiceClient.getUserByUsername(username);

        // Проверка результата.
        assertEquals(expectedUser, actualUser);

        // Проверка вызова метода exchange у RestTemplate.
        verify(restTemplate, times(1)).exchange(
                eq(apiUrl),
                eq(HttpMethod.GET),
                eq(null),
                eq(User.class),
                eq(username)
        );
    }
}
