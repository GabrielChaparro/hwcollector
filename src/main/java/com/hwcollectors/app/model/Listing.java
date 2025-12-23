package com.hwcollectors.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotwheel_id")
    private HotWheel hotwheel;

    @Enumerated(EnumType.STRING)
    private ListingType type;

    private Double price;
    private Double currentBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id")
    private User highestBidder;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<Bid> bids = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}


