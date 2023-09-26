<div style="text-align: center;">

# **GreenChat Auth**
</div>

## **Микросервис аутентификации с использованием JWT.**
____
### Описание микросервиса
Микросервис является компонентом приложения GreenChat, отвечающим за аутентификацию
и авторизацию пользователей. Он предоставляет функциональности для входа в систему, 
генерации и проверки токенов с использованием JWT (JSON Web Tokens).
Используемые технологии

* Java
* Spring Boot
* Spring Security
* JWT (JSON Web Tokens)
* Lombok
* Mockito
* JUnit
____
### Функциональности

- Вход в систему: Пользователи могут войти в систему, используя свои имя пользователя и пароль.
- Генерация токенов: При успешном входе в систему микросервис генерирует токен доступа и токен обновления.
- Проверка токенов: Микросервис может проверять токен доступа и токен обновления.
- Управление доступом: Микросервис обеспечивает контроль доступа на основе ролей к защищенным ресурсам.
____
### Установка и настройка

##### 1. Клонируйте репозиторий GreenChat Auth:
```
git clone (https://github.com/MrGreenNV/greenchat-auth-service.git)
```
##### 2. Перейдите в директорию проекта:
```
cd greenchat-auth-service
```
##### 3. Для запуска проекта в Docker воспользуйтесь следующей командой:
```
docker compose up --build -d
```

После запуска, микросервис будет доступен по адресу: http://localhost:9090.
____
### API-endpoints
Документация OpenAPI (Swagger) будет доступна после запуска проекта по ссылке: http://localhost:9090/swagger-ui/index.html#/

Микросервис GreenChat Auth предоставляет следующие API-endpoints:
##### 1. Вход в систему (Login)
```
POST /greenchat/auth/login
```
API-endpoint для выполнения операции входа в систему. Принимает JSON-объект JwtRequest с логином и хэшированным паролем пользователя.

Пример запроса:
```
{
  "username": "user@example.com",
  "password": "hashed_password"
}
```
Ответ:
```
{
  "accessToken": "access_token",
  "refreshToken": "refresh_token"
}
```
##### 2. Получение нового Access токена (Token)
```
POST /greenchat/auth/token
```
API-endpoint для получения нового Access токена на основе переданного Refresh токена. Принимает JSON-объект JwtRequestRefresh с Refresh токеном.

Пример запроса:
```
{
  "refreshToken": "refresh_token"
}
```
Ответ:
```
{
  "accessToken": "new_access_token"
}
```
##### 3. Обновление Access и Refresh токенов (Refresh)
```
POST /greenchat/auth/refresh
```
API-endpoint для обновления Access и Refresh токенов на основе переданного Refresh токена. Принимает JSON-объект JwtRequestRefresh с Refresh токеном.

Пример запроса:
```
{
  "refreshToken": "refresh_token"
}
```
Ответ:
```
{
  "accessToken": "new_access_token",
  "refreshToken": "new_refresh_token"
}
```
____
### Тестирование
Микросервис GreenChat Auth включает модульные тесты для проверки его функциональности. Вы можете запустить тесты с помощью сборщика Maven:
```
mvn test
```
____
### Вклад и обратная связь
Если вы хотите внести свой вклад в развитие GreenChat Auth или обнаружили проблему, пожалуйста, создайте issue в репозитории проекта или отправьте pull request с вашими предложениями.
____
### Документация проекта
Подробную документацию проекта GreenChat Auth вы можете найти, перейдя по ссылке:
https://mrgreennv.github.io/greenchat-auth-service
