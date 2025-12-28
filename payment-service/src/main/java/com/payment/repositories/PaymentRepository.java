package com.payment.repositories;

import com.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment> {
    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.userId = :userId AND p.timestamp >= :start AND p.timestamp <= :end")
    BigDecimal getTotalAmountByUserIdAndDateRange(@Param("userId") Integer userId,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);
    @Query("SELECT SUM(p.paymentAmount) FROM Payment p WHERE p.timestamp >= :start AND p.timestamp <= :end")
    BigDecimal getTotalAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    List<Payment> findAllByUserId(Integer userId);
}
