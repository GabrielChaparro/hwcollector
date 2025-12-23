package com.hwcollectors.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuctionClosedEvent {
    private String listingId;
    private String winner;
    private Double finalPrice;
    private LocalDateTime closedAt;
}

