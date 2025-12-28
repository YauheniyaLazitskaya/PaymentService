package com.payment.dto;

import com.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePaymentDTO {
    @NotNull(message = "Payment must have an order id.")
    @Positive(message = "Order id must me more than 0.")
    private Integer orderId;

    @NotNull(message = "Payment must have an user id.")
    @Positive(message = "User id must me more than 0.")
    private Integer userId;

    private PaymentStatus status = PaymentStatus.PENDING;
}
