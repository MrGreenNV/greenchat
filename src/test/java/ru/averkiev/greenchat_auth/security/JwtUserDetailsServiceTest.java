package ru.averkiev.greenchat_auth.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.averkiev.greenchat_auth.models.JwtUser;
import ru.averkiev.greenchat_auth.models.JwtUserFactory;
import ru.averkiev.greenchat_auth.models.User;
import ru.averkiev.greenchat_auth.clients.impl.UserServiceClientImpl;
import ru.averkiev.greenchat_auth.services.JwtUserDetailsService;

import java.util.Set;

/**
 * Тестовый класс для проверки функциональности класса JwtUserDetailsService.
 * Этот класс загружает и возвращает объект типа UserDetails по имени пользователя, используя метод
 * loadUserByUsername().
 */
public class JwtUserDetailsServiceTest {

    @Mock
    private UserServiceClientImpl userServiceClientImpl;

    private JwtUserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        // Инициализация mock-объектов и тестируемого объекта перед каждым тестом.
        MockitoAnnotations.openMocks(this);
        userDetailsService = new JwtUserDetailsService(userServiceClientImpl);
    }

    /**
     * Проверяет, что метод loadUserByUsername() возвращает объект UserDetails по полученному имени
     * пользователя username.
     */
    @Test
    public void testLoadUserByUsername() {
        // Создание тестовых данных.
        String username = "Bob_Smith";

        User user = new User(
                1,
                username,
                "pass132456",
                "Bob",
                "Smith",
                "bob@gmail.com",
                "ACTIVE",
                Set.of("ROLE_USER")
        );

        // Установка поведения mock-объекта userServiceClient.
        Mockito.when(userServiceClientImpl.getUserByLogin(username)).thenReturn(user);

        // Ожидаемый результат.
        JwtUser expectedJwtUser = JwtUserFactory.created(user);

        // Вызов тестируемого метода.
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Проверка результата.
        Assertions.assertEquals(expectedJwtUser, userDetails);
    }

    /**
     * Проверяет, что метод loadUserByUsername() выбрасывает исключение UsernameNotFoundException, если пользователь
     * не был найден по имени username.
     */
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Создание тестовых данных.
        String username = "Bob_Smith";

        // Установка поведения mock-объекта.
        Mockito.when(userServiceClientImpl.getUserByLogin(username)).thenReturn(null);

        // Проверка, что исключение UserNotFoundException выбрасывается при вызове метода loadUserByUsername().
        Assertions.assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername(username)
        );
    }
}
