package com.hwcollectors.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "collection_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotwheel_id", nullable = false)
    private HotWheel hotwheel;

    @Column(nullable = false)
    private String condition;

    private LocalDate acquiredDate;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Availability availability = Availability.NOT_FOR_SALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(name = "ask_price", precision = 10, scale = 2)
    private BigDecimal askPrice;

}
