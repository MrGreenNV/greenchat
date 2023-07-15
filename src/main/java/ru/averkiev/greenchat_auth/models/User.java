package ru.averkiev.greenchat_auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Класс предсавляет собой модель пользователя в микросервисе greenchat_auth. он содержит информацию о пользователе.
 * @author mrGrennNV
 */
@AllArgsConstructor
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String status;
    private Set<String> roles;
}