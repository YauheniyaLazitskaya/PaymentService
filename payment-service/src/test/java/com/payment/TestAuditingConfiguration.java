package com.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
@Profile("test")
public class TestAuditingConfiguration {
    @Bean
    public AuditorAware<String> testAuditorAware() {
        return () -> Optional.of("test-user");
    }
}