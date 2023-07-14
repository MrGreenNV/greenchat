package ru.averkiev.greenchat_auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.averkiev.greenchat_auth.models.User;

@Component
public class UserServiceClient {
    private final RestTemplate restTemplate;
    @Value("${user_management.url}")
    String apiUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User getUserByUsername(String username) {
        ResponseEntity<User> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                User.class,
                username
        );
        return responseEntity.getBody();
    }
}
