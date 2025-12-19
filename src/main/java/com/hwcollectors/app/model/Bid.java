package com.hwcollectors.app.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bid {
    private String bidderId;
    private Double amount;
    private LocalDateTime timestamp = LocalDateTime.now();
}
