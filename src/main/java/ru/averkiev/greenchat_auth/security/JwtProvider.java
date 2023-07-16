package ru.averkiev.greenchat_auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Класс предоставляет функциональность для создания, проверки и валидации JWT токенов.
 * @author mrGreenNV
 */
@Slf4j
@Component
public class JwtProvider {
    /**
     * Секретный ключ для подписи доступных токенов доступа.
     */
    private final SecretKey jwtAccessSecret;

    /**
     * Секретный ключ для подписи токенов обновления.
     */
    private final SecretKey jwtRefreshSecret;

    private final long expirationAccessTokenInMinutes;
    private final long expirationRefreshTokenInDays;

    @Autowired
    public JwtProvider(@Value("${jwt.secret.access}") String jwtAccessSecret,
                       @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
                       @Value("${jwt.expiration.access}") long expirationAccessTokenInMinutes,
                       @Value("${jwt.expiration.refresh}") long expirationRefreshTokenInDays
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.expirationAccessTokenInMinutes = expirationAccessTokenInMinutes;
        this.expirationRefreshTokenInDays = expirationRefreshTokenInDays;
    }

    /**
     * Генерирует и возвращает токен доступа на основе переданного объекта JwtUser. Метод создаёт токен с
     * указанным субъектом (именем пользователя), сроком действия (5 минут) и подписывает его с использованием
     * секретного ключа jwtAccessSecret. Метод также добавляет дополнительные поля такие как: имя, фамилия,
     * роли пользователя, используя данные из объекта JwtUser.
     * @param jwtUser передаваемый объект, для которого генерируется токен доступа.
     * @return строка, содержащая токен доступа.
     */
    public String generateAccessToken(@NotNull JwtUser jwtUser) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstance = now.plusMinutes(expirationAccessTokenInMinutes).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstance);
        return Jwts.builder()
                .setSubject(jwtUser.getUsername())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("firstname", jwtUser.getFirstname())
                .claim("lastname", jwtUser.getLastname())
                .claim("authorities", jwtUser.getAuthorities())
                .compact();
    }

    /**
     * Генерирует и возвращает токен обновления на основе переданного объекта JwtUser. Метод создаёт токен с
     * указанным субъектом (именем пользователя), сроком действия (7 дней) и подписывает его с использованием
     * секретного ключа jwtAccessSecret.
     * @param jwtUser передаваемый объект, для которого генерируется токен обновления.
     * @return строка, содержащая токен обновления.
     */
    public String generateRefreshToken(@NotNull JwtUser jwtUser) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(expirationRefreshTokenInDays).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(jwtUser.getUsername())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    /**
     * Проверяет и возвращает результат проверки токена доступа.
     * @param accessToken передаваемый токен доступа, который необходимо проверить.
     * @return возвращает результат проверки токена доступа.
     */
    public boolean validateAccessToken(@NotNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    /**
     * Проверяет и возвращает результат проверки токена обновления.
     * @param refreshToken передаваемый токен доступа, который необходимо проверить.
     * @return возвращает результат проверки токена обновления.
     */
    public boolean validateRefreshToken(@NotNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    /**
     * Проверяет и возвращает результат проверки токена.
     * @param token переданный токен, который необходимо проверить.
     * @param secret секретный ключ для разбора токена и проверки его целостности.
     * @return возвращает результат проверки токена.
     * @exception ExpiredJwtException выбрасывается если токен недействителен.
     * @exception UnsupportedJwtException выбрасывается если токен не поддерживается.
     * @exception MalformedJwtException выбрасывается если токен некорректен.
     * @exception SignatureException выбрасывается если секретный ключ недействителен.
     */
    public boolean validateToken(@NotNull String token, @NotNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Истек срок действия токена", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Неподдерживаемый JWT", unsEx);
        } catch (MalformedJwtException malEx) {
            log.error("Некорректный JWT", malEx);
        } catch (SignatureException sEx) {
            log.error("Недействительная подпись", sEx);
        } catch (Exception ex) {
            log.error("Неправильный токен", ex);
        }
        return false;
    }

    /**
     * Извлекает и возвращает объект Claims из разобранного токена. Метод использует парсер для разбора токена и
     * извлечения полезной нагрузки (payload) токена.
     * @param token передаваемый токен, из которого необходимо извлечь объект Claims.
     * @param secret секретный ключ для разбора передаваемого токена.
     * @return объект Claims, содержащий payload переданного токена.
     */
    private Claims getClaims(@NotNull String token, @NotNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Извлекает и возвращает дату и время создания access токена.
     * @param accessTokenStr access токен.
     * @return Data - дата и время создания access токена.
     */
    public Date getCreatedAtAccessToken(String accessTokenStr) {
        return Date.from(getClaims(accessTokenStr, jwtAccessSecret).getExpiration().toInstant().minusSeconds(expirationAccessTokenInMinutes * 60));
    }

    /**
     * Извлекает и возвращает дату и время истечения срока действия access токена.
     * @param accessTokenStr access токен.
     * @return Data - дата и время истечения срока действия access токена.
     */
    public Date getExpiredAtAccessToken(String accessTokenStr) {
        return getClaims(accessTokenStr, jwtAccessSecret).getExpiration();
    }

    /**
     * Извлекает и возвращает дату и время создания refresh токена.
     * @param refreshToken access токен.
     * @return Data - дата и время создания access токена.
     */
    public Date getCreatedAtRefreshToken(String refreshToken) {
        return Date.from(getClaims(refreshToken, jwtRefreshSecret).getExpiration().toInstant().minusSeconds(expirationRefreshTokenInDays * 60 * 60 * 24));
    }

    /**
     * Извлекает и возвращает дату и время истечения срока действия refresh токена.
     * @param refreshToken access токен.
     * @return Data - дата и время истечения срока действия refresh токена.
     */
    public Date getExpiredAtRefreshToken(String refreshToken) {
        return getClaims(refreshToken, jwtRefreshSecret).getExpiration();
    }
}