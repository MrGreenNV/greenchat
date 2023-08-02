package ru.averkiev.greenchat_auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Класс представляет собой модель пользователя в микросервисе greenchat_auth. Он содержит информацию о пользователе.
 * @author mrGrennNV
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String status;
    private Set<String> roles;
}