package com.payment.clients;

import com.common.dto.orderDto.OrderDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "order-service", url = "${application.config.order-service-url}")
public interface OrderFeignClient {
    @GetMapping("/api/users/{userId}/orders/{orderId}")
    OrderDataDTO getOrderById(@PathVariable("userId") Integer userId, @PathVariable("orderId") Integer orderId);
}