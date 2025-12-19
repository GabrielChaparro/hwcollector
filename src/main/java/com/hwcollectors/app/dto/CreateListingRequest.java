package com.hwcollectors.app.dto;

import com.hwcollectors.app.model.ListingType;
import lombok.Data;


@Data
public class CreateListingRequest {
    private String hotwheelId;
    private ListingType type;
    private Double price;
    private Integer durationHours; // solo para AUCTION
}
