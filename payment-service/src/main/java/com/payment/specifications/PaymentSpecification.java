package com.payment.specifications;

import com.payment.entities.Payment;
import com.payment.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;


public class PaymentSpecification {
    public static Specification<Payment> hasUserId(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
