package com.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionDTO {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
