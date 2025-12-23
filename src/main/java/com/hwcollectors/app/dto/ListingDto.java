package com.hwcollectors.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListingDto {
    private Long id;
    private String hotwheelCode;
    private String hotwheelName;
    private String sellerEmail;
    private String type;
    private Double price;
    private Double currentBid;
    private String status;
    private LocalDateTime endDate;
}

