package com.hwcollectors.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    private String id;
    private String sellerId;
    private String hotwheelId;
    private ListingType type; // FIXED, AUCTION
    private Double price; // precio fijo o puja inicial
    private Double currentBid;
    private String highestBidderId;
    private LocalDateTime endDate; // para subastas
    private ListingStatus status; // ACTIVE, SOLD, CANCELLED
    private List<Bid> bids = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
}

