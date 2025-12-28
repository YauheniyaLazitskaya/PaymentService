package com.payment.dto;

import com.payment.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentFilterDTO {
    PaymentStatus status;
    Integer userId;
}
