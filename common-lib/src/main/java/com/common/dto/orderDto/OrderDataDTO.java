package com.common.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDataDTO {
    private Integer id;
    private BigDecimal totalPrice;
    private Integer userId;
}
