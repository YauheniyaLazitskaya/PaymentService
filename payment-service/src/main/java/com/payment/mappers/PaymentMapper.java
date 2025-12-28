package com.payment.mappers;

import com.payment.dto.CreatePaymentDTO;
import com.payment.dto.PaymentDTO;
import com.payment.entities.Payment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDTO toDto(Payment payment);
    Payment toEntity(PaymentDTO paymentDTO);
    Payment toEntity(CreatePaymentDTO createPaymentDTO);
    List<PaymentDTO> toDtoList(List<Payment> payments);
}
