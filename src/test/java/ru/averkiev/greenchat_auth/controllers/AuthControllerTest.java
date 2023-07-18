package ru.averkiev.greenchat_auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.averkiev.greenchat_auth.models.JwtRequest;
import ru.averkiev.greenchat_auth.models.JwtRequestRefresh;
import ru.averkiev.greenchat_auth.models.JwtResponse;
import ru.averkiev.greenchat_auth.services.impl.AuthServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тестовый класс для проверки функциональности AuthController
 */
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Проверяет аутентификацию пользователя при входе в систему.
     * Ожидается успешный ответ с access и refresh токенами.
     */
    @Test
    public void testLogin() throws Exception {
        // Создание тестовых данных.
        JwtRequest jwtRequest = new JwtRequest("test_user", encoder.encode("testPassword"));
        JwtResponse jwtResponse = new JwtResponse("access_token", "refresh_token");

        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        // Вызов тестируемого метода.
        authController.login(jwtRequest);

        mockMvc.perform(post("/greenchat/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token")

        );

        // Проверка результатов.
        verify(authService).login(jwtRequest);
    }

    /**
     * Проверяет получение нового access токена на основе переданного refresh токена.
     * Ожидается успешный ответ включающий в себя access токен и значение null вместо refresh токена.
     */
    @Test
    public void testGetNewAccessToken() throws Exception {
        // Создание тестовых данных.
        JwtRequestRefresh jwtRequestRefresh = new JwtRequestRefresh();
        jwtRequestRefresh.setRefreshToken("refresh_token");
        JwtResponse jwtResponse = new JwtResponse("access_token", null);

        when(authService.getAccessToken(jwtRequestRefresh.getRefreshToken())).thenReturn(jwtResponse);

        // Вызов тестируемого метода.
        mockMvc.perform(post("/greenchat/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jwtRequestRefresh)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").isEmpty()
                );

        // Проверка результатов.
        verify(authService).getAccessToken(jwtRequestRefresh.getRefreshToken());
    }

    /**
     * Проверяет обновление access и refresh токенов на основе переданного refresh токена.
     * Ожидается успешный ответ с access и refresh токенами.
     */
    @Test
    public void testGetNewRefreshToken() throws Exception {
        // Создание тестовых данных.
        JwtRequestRefresh jwtRequestRefresh = new JwtRequestRefresh();
        jwtRequestRefresh.setRefreshToken("test_refresh_token");
        JwtResponse jwtResponse = new JwtResponse("test_access_token", "test_refresh_token");

        when(authService.refresh(jwtRequestRefresh.getRefreshToken())).thenReturn(jwtResponse);

        // Вызов тестируемого метода.
        mockMvc.perform(post("/greenchat/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jwtRequestRefresh)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("test_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("test_refresh_token")
        );

        // Проверка результатов.
        verify(authService).refresh(jwtRequestRefresh.getRefreshToken());
    }
}