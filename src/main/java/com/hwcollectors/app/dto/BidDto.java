package com.hwcollectors.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BidDto {
    private Long id;
    private Double amount;
    private String bidderEmail;
    private LocalDateTime timestamp;
}

