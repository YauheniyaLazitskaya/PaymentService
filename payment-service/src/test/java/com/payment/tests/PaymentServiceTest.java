package com.payment.tests;

import com.common.dto.orderDto.OrderDataDTO;
import com.common.dto.paymentDto.PaymentEventDTO;
import com.payment.clients.OrderFeignClient;
import com.payment.dto.CreatePaymentDTO;
import com.payment.dto.PaymentDTO;
import com.payment.entities.Payment;
import com.payment.enums.PaymentStatus;
import com.payment.mappers.PaymentMapper;
import com.payment.repositories.PaymentRepository;
import com.payment.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private OrderFeignClient orderFeignClient;
    @Mock
    private KafkaTemplate<String, PaymentEventDTO> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentSuccess() {
        Integer userId = 1;
        Integer orderId = 100;
        BigDecimal amount = BigDecimal.valueOf(500);

        CreatePaymentDTO createDto = new CreatePaymentDTO();
        createDto.setUserId(userId);
        createDto.setOrderId(orderId);

        OrderDataDTO mockOrder = new OrderDataDTO();
        mockOrder.setId(orderId);
        mockOrder.setUserId(userId);
        mockOrder.setTotalPrice(amount);

        Payment paymentEntity = new Payment();
        paymentEntity.setId(1);
        paymentEntity.setStatus(PaymentStatus.SUCCESS);
        paymentEntity.setPaymentAmount(amount);
        paymentEntity.setOrderId(orderId);
        paymentEntity.setUserId(userId);

        PaymentDTO expectedDto = new PaymentDTO();
        expectedDto.setId(1);
        expectedDto.setStatus(PaymentStatus.SUCCESS);
        expectedDto.setPaymentAmount(amount);
        expectedDto.setOrderId(orderId);
        expectedDto.setUserId(userId);

        when(orderFeignClient.getOrderById(userId, orderId)).thenReturn(mockOrder);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(expectedDto);

        PaymentDTO result = paymentService.createPayment(createDto);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());

        verify(orderFeignClient).getOrderById(userId, orderId);
        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq("payment-events"), eq(orderId.toString()), any(PaymentEventDTO.class));
    }

    @Test
    void createPayment_UserMismatch_ThrowsException() {
        CreatePaymentDTO createDto = new CreatePaymentDTO();
        createDto.setUserId(1);
        createDto.setOrderId(100);

        OrderDataDTO mockOrder = new OrderDataDTO();
        mockOrder.setUserId(2);

        when(orderFeignClient.getOrderById(1, 100)).thenReturn(mockOrder);

        assertThrows(RuntimeException.class, () -> paymentService.createPayment(createDto));

        verify(paymentRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}