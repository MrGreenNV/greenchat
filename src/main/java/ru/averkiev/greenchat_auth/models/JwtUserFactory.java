package ru.averkiev.greenchat_auth.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс предоставляет статические методы для создания объекта JwtUser из объекта User и преобразования ролей
 * в коллекцию GrantedAuthority. Этот класс упрощает процесс создания JwtUser для использования в генерации и проверке
 * JWT токенов в greenchat.
 * @author mrGreenNV
 */
public class JwtUserFactory {

    private JwtUserFactory() {

    }

    /**
     * Создаёт и возвращает объект JwtUser на основе переданного объекта User. Преобразует поля объекта User
     * в соответствующие поля JwtUser и преобразует роли пользователя в коллекцию объектов GrantedAuthority.
     * @param user - переданный пользователь, которого необходимо преобразовать в объект JwtUser
     * @return - объект JwtUser, в которого преобразован User
     */
    public static JwtUser created(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getStatus().equals("ACTIVE"),
                mapToGrantedAuthorities(new HashSet<>(user.getRoles()))
        );
    }

    /**
     * Преобразует множество строковых ролей пользователя в множество объектов GrantedAuthority. Использует
     * метод stream() для преобразования каждой строки в объект SimpleGrantedAuthority и собирает результаты в
     * новое множество с помощью Collectors.toSet().
     * @param userRoles - список String ролей пользователя в структуре Set.
     * @return - список SimpleGrantedAuthority в структуре Set ролей пользователя.
     */
    private static Set<GrantedAuthority> mapToGrantedAuthorities(Set<String> userRoles) {
        return userRoles.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}