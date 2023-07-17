package ru.averkiev.greenchat_auth.services;

import ru.averkiev.greenchat_auth.security.JwtAuthentication;
import ru.averkiev.greenchat_auth.security.JwtRequest;
import ru.averkiev.greenchat_auth.security.JwtResponse;

/**
 * @author mrGreenNV
 */
public interface AuthService {
    JwtResponse login(JwtRequest authRequest);
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refresh(String refreshToken);
    JwtAuthentication getAuthInfo();
}