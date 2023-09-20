package ru.averkiev.greenchat_auth.utils;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Класс для настройки конфигурации Swagger.
 * @author mrGreenNV
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service Green_Chat",
                description = "Микросервис аутентификации и авторизации, позволяет клиенту осуществить аутентификацию" +
                        " и авторизацию в системе и получить access и refresh JWT токены. Микросервис также способен " +
                        "выполнить обновление токенов и осуществить их валидацию.", version = "1.0.0",
                contact = @Contact(
                        name = "mrGreenNV",
                        email = "averkievnv@gmail.com",
                        url = "https://github.com/MrGreenNV"
                )
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
