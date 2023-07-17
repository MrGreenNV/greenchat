package ru.averkiev.greenchat_auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Класс представляет собой модель refresh токена.
 * @author mrGreenNV
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    public RefreshToken(int userId, String refreshToken, Date createdAt, Date expiresAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;
}