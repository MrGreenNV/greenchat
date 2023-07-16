package ru.averkiev.greenchat_auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Класс представляет собой модель access токена.
 * @author mrGreenNV
 */
@Entity
@Table(name = "access_tokens")
@Getter
@Setter
@NoArgsConstructor
public class AccessToken {

    public AccessToken(int userId, String accessToken, Date createdAt, Date expiresAt) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;
}