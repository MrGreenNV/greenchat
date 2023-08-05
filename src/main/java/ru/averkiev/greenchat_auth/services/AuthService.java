package ru.averkiev.greenchat_auth.services;

import org.springframework.security.core.Authentication;
import ru.averkiev.greenchat_auth.security.JwtAuthentication;
import ru.averkiev.greenchat_auth.models.JwtRequest;
import ru.averkiev.greenchat_auth.models.JwtResponse;

/**
 * @author mrGreenNV
 */
public interface AuthService {
    JwtResponse login(JwtRequest authRequest);
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refresh(String refreshToken);
    JwtAuthentication getAuthInfo();
    boolean logout(String refreshToken);
    boolean validate(String refreshToken);
    Authentication getAuthentication(String token);
}