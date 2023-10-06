package ru.averkiev.greenchat_auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.averkiev.greenchat_auth.models.JwtRequest;
import ru.averkiev.greenchat_auth.models.JwtRequestRefresh;
import ru.averkiev.greenchat_auth.models.JwtResponse;
import ru.averkiev.greenchat_auth.services.AuthService;

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

    private final AuthService authService;

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
     * @return HttpStatus запроса.
     */
    @PostMapping("logout")
    public ResponseEntity<HttpStatus> logout(@RequestBody JwtRequestRefresh request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * API-endpoint для проверки валидности refresh токена.
     * @param request POST запрос с объектом JwtRequestRefresh, содержащим refresh токен.
     * @return HttpStatus запроса.
     */
    @PostMapping("validate")
    public ResponseEntity<HttpStatus> validate(@RequestBody JwtRequestRefresh request) {
        authService.validate(request.getRefreshToken());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}