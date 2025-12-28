package com.payment.services;

import com.payment.clients.UserFeignClient;
import com.common.dto.userDto.UserDataDTO;
import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserIntegrationService {

    private final UserFeignClient userFeignClient;

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackGetUser")
    public UserDataDTO getUserInfoSafe(Integer userId) {
        return userFeignClient.getUserById(userId);
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackGetUser")
    public UserDataDTO getUserByKeycloakId(String keycloakId) {
        return userFeignClient.getUserByKeycloakId(keycloakId);
    }

    public UserDataDTO fallbackGetUser(Integer userId, Throwable t) {
        System.err.println("User Service is down! Error: " + t.getMessage());
        return new UserDataDTO(userId, "Service Unavailable", "", "unknown", null);
    }
}