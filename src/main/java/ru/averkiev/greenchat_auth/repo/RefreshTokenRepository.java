package ru.averkiev.greenchat_auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.averkiev.greenchat_auth.models.RefreshToken;

import java.util.Optional;

/**
 * Интерфейс представляет собой репозиторий refresh токенов.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByUserId(int UserId);
    void deleteByUserId(int userId);
}
