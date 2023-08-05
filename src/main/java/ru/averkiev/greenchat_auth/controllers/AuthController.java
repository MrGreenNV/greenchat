package ru.averkiev.greenchat_auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.averkiev.greenchat_auth.exceptions.AuthException;
import ru.averkiev.greenchat_auth.models.JwtRequest;
import ru.averkiev.greenchat_auth.models.JwtRequestRefresh;
import ru.averkiev.greenchat_auth.models.JwtResponse;
import ru.averkiev.greenchat_auth.models.User;
import ru.averkiev.greenchat_auth.services.impl.AuthServiceImpl;

import java.util.stream.Collectors;

/**
 * Класс представляет собой REST-контроллер для аутентификации и авторизации пользователей в системе.
 * Этот класс предоставляет API-endpoints для выполнения операций входа в систему, получения новых и обновления JWT
 * токенов. Все запросы выполняются в формате JSON.
 * @author mrGreenNV
 */
@RestController
@RequestMapping("greenchat/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * API-endpoint для выполнения операции входа в систему.
     * @param jwtRequest POST запрос с объектом JwtRequest, содержащим логин и хэшированный пароль пользователя.
     * @return ResponseEntity с объектом JwtResponse, содержащим access и refresh токены.
     */
    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        final JwtResponse token = authService.login(jwtRequest);
        return ResponseEntity.ok(token);
    }

    /**
     * API-endpoint для получения нового access токена на основе переданного refresh токена.
     * @param request POST запрос с объектом JwtRequestRefresh, содержащим refresh токен.
     * @return ResponseEntity с объектом JwtResponse, содержащим access токен.
     */
    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody JwtRequestRefresh request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /**
     * API-endpoint для обновления access и refresh токенов на основе переданного refresh токена.
     * @param request POST запрос с объектом JwtRequestRefresh, содержащим refresh токен.
     * @return ResponseEntity с объектом JwtResponse, содержащим access и refresh токены.
     */
    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody JwtRequestRefresh request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /**
     * API-endpoint для выхода пользователя из системы путём уделения токенов.
     * @param request POST запрос с объектом JwtRequestRefresh, содержащим refresh токен.
     * @return Статус операции (true или false).
     */
    @PostMapping("logout")
    public ResponseEntity<Boolean> logout(@RequestBody JwtRequestRefresh request) {
        if (authService.logout(request.getRefreshToken())) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    /**
     * API-endpoint для проверки валидности refresh токена.
     * @param request POST запрос с объектом JwtRequestRefresh, содержащим refresh токен.
     * @return Статус валидности токена.
     */
    @PostMapping("validate")
    public ResponseEntity<Boolean> validate(@RequestBody JwtRequestRefresh request) {
        if (authService.validate(request.getRefreshToken())) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    /**
     * Api-endpoint для проверки валидности access токена и получения аутентификации.
     * @param authorizationHeader заголовок с данными авторизации.
     * @return пользователя с данными из токена.
     */
    @GetMapping("validate")
    public ResponseEntity<User> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        // Получаем токен из заголовка Authorization
        String token;
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            throw new AuthException("Невалидный header");
        }

        // Выполняем проверку токена и получаем объект Authentication
        Authentication authentication = authService.getAuthentication(token);

        if (authentication != null) {
            User user = new User();
            user.setLogin((String) authentication.getPrincipal());
            user.setPassword((String) authentication.getCredentials());
            user.setRoles(authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            return ResponseEntity.ok(user);
        } else {
            // Если токен недействителен, возвращаем ошибку или другой статус
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}