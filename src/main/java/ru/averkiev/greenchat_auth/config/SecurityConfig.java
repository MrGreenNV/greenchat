package ru.averkiev.greenchat_auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * Класс представляет собой конфигурацию безопасности для микросервиса аутентификации и авторизации.
 * Он определяет основные настройки и компоненты, связанные с безопасностью, используемые в микросервисе.
 * @author mrGreenNV
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Создает экземпляр объекта RestTemplate, который будет использоваться для взаимодействия с другими
     * микросервисами через HTTP.
     * @return возвращает экземпляр объекта RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Создаёт экземпляр объекта BCryptPasswordEncoder, который будет использоваться для хеширования паролей.
     * @return экземпляр объекта BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
