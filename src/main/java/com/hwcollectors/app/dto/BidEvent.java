package com.hwcollectors.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BidEvent {
    private String listingId;
    private Double amount;
    private String bidderName;
    private LocalDateTime timestamp;
}
