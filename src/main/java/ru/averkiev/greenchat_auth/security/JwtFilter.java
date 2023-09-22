package ru.averkiev.greenchat_auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ru.averkiev.greenchat_auth.exceptions.AuthException;
import ru.averkiev.greenchat_auth.utils.JwtUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Класс JwtFilter является фильтром Spring Security, который обрабатывает HTTP-запросы,
 * содержащие JSON Web Token (JWT).
 * Фильтр извлекает токен из заголовка Authorization, проверяет его с использованием JwtProvider,
 * и если токен действительный, создает объект JwtAuthentication, представляющий информацию о пользователе из JWT,
 * и устанавливает его в контекст безопасности.
 * @author mrGreenNV
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;

    /**
     * Метод doFilter обрабатывает HTTP-запросы, проходящие через данный фильтр.
     * Он извлекает JSON Web Token (JWT) из заголовка Authorization HTTP-запроса,
     * сравнивает его с помощью JwtProvider, и если токен действителен,
     * создает объект JwtAuthentication с помощью JwtUtils.generate и устанавливает его в контекст безопасности
     * с помощью SecurityContextHolder.getContext().setAuthentication(jwtInfoToken).
     * После успешной обработки, запрос передается на следующий фильтр или обработчик.
     *
     * @param servletRequest  HTTP-запрос.
     * @param servletResponse HTTP-ответ.
     * @param filterChain     цепочка фильтров для обработки запроса.
     * @throws IOException      исключение ввода/вывода.
     * @throws ServletException исключение сервлета.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final String token = getTokenFromRequest((HttpServletRequest) servletRequest);

        try {
            if (token != null && jwtProvider.validateAccessToken(token)) {
                final Claims claims = jwtProvider.getAccessClaims(token);
                final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
                jwtInfoToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (AuthException e) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("\"timestamp\": \"" + LocalDateTime.now() + "\",");
            response.getWriter().write("\"status\": \"" + HttpStatus.UNAUTHORIZED.name() + "\",");
            response.getWriter().write("\"error\": \"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\",");
            response.getWriter().write("\"errorMessage\": \"" + e.getMessage() + "\",");
            response.getWriter().write("\"path\": \"" + ((HttpServletRequest) servletRequest).getRequestURI() + "\",");
            response.getWriter().write("\"errors\": null");
        }
    }

    /**
     * Приватный метод getTokenFromRequest извлекает JSON Web Token (JWT) из заголовка Authorization HTTP-запроса.
     * Если токен присутствует и начинается с префикса "Bearer ", метод возвращает сам токен (без префикса),
     * в противном случае возвращается null.
     *
     * @param servletRequest HTTP-запрос.
     * @return JSON Web Token (JWT) или null, если он не найден в заголовке.
     */
    private String getTokenFromRequest(HttpServletRequest servletRequest) {
        final String bearer = servletRequest.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
