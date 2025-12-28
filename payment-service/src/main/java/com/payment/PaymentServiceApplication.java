package com.payment;

import com.common.config.CacheConfig;
import com.common.config.JpaAuditingConfiguration;
import com.common.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.payment", "com.common"})
@EnableFeignClients
@Import({SecurityConfig.class, CacheConfig.class, JpaAuditingConfiguration.class})
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
