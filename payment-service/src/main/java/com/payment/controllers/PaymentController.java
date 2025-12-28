package com.payment.controllers;

import com.payment.dto.CreatePaymentDTO;
import com.payment.dto.PaymentDTO;
import com.payment.dto.PaymentFilterDTO;
import com.payment.enums.PaymentStatus;
import com.payment.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/users/{userId}/payments")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<PaymentDTO> createPayment( @PathVariable Integer userId,
                                                     @Valid @RequestBody CreatePaymentDTO createPaymentDTO) {
        PaymentDTO paymentDTO = paymentService.createPayment(createPaymentDTO);
        return new ResponseEntity<>(paymentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/payments")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(paymentService.getAllPaymentsByUserId(userId));
    }

    @GetMapping("/users/{userId}/payments/summary")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<BigDecimal> getUserTotalAmount( @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        BigDecimal total = paymentService.getTotalAmountByUserId(userId, from, to);
        return ResponseEntity.ok(total);
    }

    @PatchMapping("/payments/{paymentId}/status")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PaymentDTO> updateStatus( @PathVariable Integer paymentId,
                                                    @RequestParam PaymentStatus status) {
        PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments( @ModelAttribute PaymentFilterDTO filter,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getAllPayments(filter, page, size));
    }

    @GetMapping("/payments/summary")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<BigDecimal> getGlobalTotalAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        BigDecimal total = paymentService.getTotalAmount(from, to);
        return ResponseEntity.ok(total);
    }
}