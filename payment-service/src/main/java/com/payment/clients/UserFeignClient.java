package com.payment.clients;

import com.common.dto.userDto.UserDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${application.config.user-service-url}")
public interface UserFeignClient {
    @GetMapping("/api/users/{id}")
    UserDataDTO getUserById(@PathVariable("id") Integer id);

    @GetMapping("/api/users/by-email")
    UserDataDTO getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/api/users/by-keycloak/{keycloakId}")
    UserDataDTO getUserByKeycloakId(@PathVariable("keycloakId") String keycloakId);
}
