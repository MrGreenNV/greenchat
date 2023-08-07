package ru.averkiev.greenchat_auth.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.averkiev.greenchat_auth.models.JwtUser;
import ru.averkiev.greenchat_auth.models.JwtUserFactory;
import ru.averkiev.greenchat_auth.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Тестовый класс для проверки функциональности класса JwtUserFactory.
 * Этот класс предоставляет статические методы для создания объекта JwtUser из объекта User и преобразования ролей
 * в коллекцию GrantedAuthority.
 */
public class JwtUserFactoryTest {

    /**
     * Проверяет, что метод JwtUserCreation создаёт корректный объект JwtUser по полученному объекту User, при этом
     * преобразуя список строковых ролей в список объектов SimpleGrantedAuthority.
     */
    @Test
    public void testJwtUserCreation() {
        //Создание тестовых данных
        User user = new User(
                1,
                "Bob_Smith",
                "Bob",
                "Smith",
                "pass123456",
                "bob@gmail.com",
                "ACTIVE",
                List.of("ROLE_USER", "ROLE_ADMIN")

        );

        // Создание ожидаемого результата
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        JwtUser expectedJwtUser = new JwtUser(
                1,
                "Bob_Smith",
                "Bob",
                "Smith",
                "pass123456",
                "bob@gmail.com",
                true,
                authorities
        );

        // Вызов тестируемого метода
        JwtUser createdJwtUser = JwtUserFactory.created(user);

        // Проверка результата
        Assertions.assertEquals(expectedJwtUser, createdJwtUser);
    }
}