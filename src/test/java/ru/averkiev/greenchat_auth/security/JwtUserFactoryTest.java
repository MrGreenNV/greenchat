package ru.averkiev.greenchat_auth.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.averkiev.greenchat_auth.models.User;

import java.util.HashSet;
import java.util.Set;

public class JwtUserFactoryTest {

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
                Set.of("ROLE_USER", "ROLE_ADMIN")

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