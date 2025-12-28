package com.payment.services;

import com.common.dto.orderDto.OrderDataDTO;
import com.common.dto.paymentDto.PaymentEventDTO;
import com.payment.clients.OrderFeignClient;
import com.payment.dto.CreatePaymentDTO;
import com.payment.dto.PaymentDTO;
import com.payment.dto.PaymentFilterDTO;
import com.payment.entities.Payment;
import com.payment.enums.PaymentStatus;
import com.payment.exceptions.PaymentException;
import com.payment.mappers.PaymentMapper;
import com.payment.repositories.PaymentRepository;
import com.payment.specifications.PaymentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderFeignClient orderFeignClient;
    private final KafkaTemplate<String, PaymentEventDTO> kafkaTemplate;

    @Transactional
    public PaymentDTO createPayment(CreatePaymentDTO createPaymentDTO) {
        OrderDataDTO orderDataDTO;
        try {
            orderDataDTO = orderFeignClient.getOrderById(createPaymentDTO.getUserId(),
                    createPaymentDTO.getOrderId());
            if(orderDataDTO==null)
                throw new PaymentException("Order data not found for payment.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new PaymentException("Order data not found for payment. " + e.getMessage());
        }
        if (!orderDataDTO.getUserId().equals(createPaymentDTO.getUserId())) {
            throw new PaymentException("Payment user is not the owner of the order.");
        }
        Payment payment = new Payment();
        payment.setUserId(createPaymentDTO.getUserId());
        payment.setOrderId(createPaymentDTO.getOrderId());
        payment.setStatus(createPaymentDTO.getStatus());
        payment.setPaymentAmount(orderDataDTO.getTotalPrice());
        Payment savedPayment = paymentRepository.save(payment);

        PaymentEventDTO event = new PaymentEventDTO(
                savedPayment.getOrderId(),
                savedPayment.getUserId(),
                savedPayment.getStatus().name(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("payment-events", savedPayment.getOrderId().toString(), event);

        return paymentMapper.toDto(savedPayment);
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + paymentId + "."));
        payment.setStatus(paymentStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(updatedPayment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(PaymentFilterDTO filter, int page, int size) {
        Specification<Payment> spec = Specification.where(null);
        if (filter != null) {
            spec = spec.and(PaymentSpecification.hasStatus(filter.getStatus()))
                    .and(PaymentSpecification.hasUserId(filter.getUserId()));
        }
        return paymentRepository.findAll(spec, PageRequest.of(page, size))
                .map(payment -> {
                    PaymentDTO dto = paymentMapper.toDto(payment);
                    return dto;});
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPaymentsByUserId(Integer userId) {
        List<Payment> payments = paymentRepository.findAllByUserId(userId);
        if(payments.isEmpty()) return Collections.emptyList();
        return paymentMapper.toDtoList(payments);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUserId(Integer userId, LocalDateTime from, LocalDateTime to) {
        BigDecimal total = paymentRepository.getTotalAmountByUserIdAndDateRange(userId, from, to);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmount(LocalDateTime from, LocalDateTime to) {
        BigDecimal total = paymentRepository.getTotalAmountByDateRange(from, to);
        return total != null ? total : BigDecimal.ZERO;
    }
}
