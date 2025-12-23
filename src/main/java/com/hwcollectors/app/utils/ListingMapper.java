package com.hwcollectors.app.utils;

import com.hwcollectors.app.dto.ListingDto;
import com.hwcollectors.app.model.Listing;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListingMapper {

    public ListingDto toDto(Listing listing) {
        ListingDto dto = new ListingDto();
        dto.setId(listing.getId());
        dto.setType(listing.getType().name());
        dto.setPrice(listing.getPrice());
        dto.setCurrentBid(listing.getCurrentBid());
        dto.setStatus(listing.getStatus().name());
        dto.setEndDate(listing.getEndDate());

        if (listing.getSeller() != null) {
            dto.setSellerEmail(listing.getSeller().getEmail());
        }
        if (listing.getHotwheel() != null) {
            dto.setHotwheelCode(listing.getHotwheel().getCode());
            dto.setHotwheelName(listing.getHotwheel().getName());
        }
        return dto;
    }

    public List<ListingDto> toDtoList(List<Listing> listings) {
        return listings.stream().map(this::toDto).toList();
    }
}

