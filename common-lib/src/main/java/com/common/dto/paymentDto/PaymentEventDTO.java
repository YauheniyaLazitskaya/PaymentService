package com.common.dto.paymentDto; // или events

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEventDTO {
    private Integer orderId;
    private Integer userId;
    private String status;
    private LocalDateTime eventDate;
}