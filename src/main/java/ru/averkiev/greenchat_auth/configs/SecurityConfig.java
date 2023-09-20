package ru.averkiev.greenchat_auth.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import ru.averkiev.greenchat_auth.security.JwtFilter;

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
     * Фильтр запросов.
     */
    private final JwtFilter jwtFilter;

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

    /**
     * Позволяет настроить httpSecurity
     * @param httpSecurity параметр безопасности http.
     * @return объект SecurityFilterChain
     * @throws Exception выбрасывает, если появляется ошибка во время выполнения фильтрации запросов
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
