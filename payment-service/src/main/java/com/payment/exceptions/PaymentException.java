package com.payment.exceptions;

import com.common.exceptions.AppException;

public class PaymentException extends AppException {
    public PaymentException(String message) {
        super(message);
    }
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
