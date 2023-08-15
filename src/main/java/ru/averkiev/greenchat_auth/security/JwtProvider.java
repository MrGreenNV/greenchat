package ru.averkiev.greenchat_auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.averkiev.greenchat_auth.models.JwtUser;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

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
     * указанным субъектом (именем пользователя), сроком действия и подписывает его с использованием
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
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(jwtAccessSecret)
                .claim("firstname", jwtUser.getFirstname())
                .claim("lastname", jwtUser.getLastname())
                .claim("authorities", jwtUser.getAuthorities())
                .compact();
    }

    /**
     * Генерирует и возвращает токен обновления на основе переданного объекта JwtUser. Метод создаёт токен с
     * указанным субъектом (именем пользователя), сроком действия и подписывает его с использованием
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
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
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
     * Извлекает и возвращает объект Claims из разобранного access токена, с помощью вызова метода getClaims.
     * @param AccessToken - токен, из которого извлекаются Claims.
     * @return объект Claims, содержащий body переданного токена.
     */
    public Claims getAccessClaims(@NotNull String AccessToken) {
        return getClaims(AccessToken, jwtAccessSecret);
    }

    /**
     * Извлекает и возвращает объект Claims из разобранного refresh токена, с помощью вызова метода getClaims.
     * @param refreshToken - токен, из которого извлекаются Claims.
     * @return объект Claims, содержащий body переданного токена.
     */
    public Claims getRefreshClaims(@NotNull String refreshToken) {
        return getClaims(refreshToken, jwtRefreshSecret);
    }

    /**
     * Проверяет access токен и возвращает объект авторизации.
     * @param token access токен
     * @return объект UsernamePasswordAuthenticationToken с данными авторизации.
     */
    public Authentication validateToken(String token) {
        try {
            // Разбираем токен и проверяем его подпись
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // В данном примере предполагается, что в токене хранятся данные об аутентификации пользователя,
            // которые могут быть извлечены из объекта Claims и использованы для создания объекта Authentication.
            // Вам нужно заменить этот код на извлечение данных из Claims и создание объекта Authentication,
            // соответствующее вашей логике аутентификации и авторизации.
            System.out.println(claims.getSubject());

            List<LinkedHashMap<String, String>> authMap = (List<LinkedHashMap<String, String>>) claims.get("authorities");

            List<String> grantedAuthorities = authMap.stream()
                    .map(authMaps -> authMaps.get("authority"))
                    .collect(Collectors.toList());

            System.out.println(grantedAuthorities);

            return new UsernamePasswordAuthenticationToken(claims.getSubject(),
                    null,
                    grantedAuthorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            // Если токен недействителен или произошла ошибка при его проверке,
            // возвращаем null или бросаем исключение
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Извлекает и возвращает объект Claims из разобранного токена. Метод использует парсер для разбора токена и
     * извлечения полезной нагрузки (payload) токена.
     * @param token передаваемый токен, из которого необходимо извлечь объект Claims.
     * @param secret секретный ключ для разбора передаваемого токена.
     * @return объект Claims, содержащий body переданного токена.
     */
    private Claims getClaims(@NotNull String token, @NotNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Извлекает из Claims воемя окончания токена и сравнивает с текущим временем.
     * @param token access токен.
     * @return true если токен просуществовал больше половины отведенного времени, иначе - false.
     */
    public boolean isAccessTokenExpired(String token) {
        Claims claims = this.getAccessClaims(token);

        // Время окончания действия текущего токена.
        final Date currentAccessExpiration = claims.getExpiration();

        // Текущее время.
        final Date currentDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        // Если разница текущего времени со временем окончания токена больше половины срока действия токена.
        if (currentDate.getTime() - currentAccessExpiration.getTime() > (expirationAccessTokenInMinutes * 30 * 1000)) {
            return false;
        } else {
            return true;
        }
    }
}