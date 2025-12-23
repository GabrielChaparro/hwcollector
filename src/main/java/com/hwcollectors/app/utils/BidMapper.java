package com.hwcollectors.app.utils;

import com.hwcollectors.app.dto.BidDto;
import com.hwcollectors.app.model.Bid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BidMapper {
    public BidDto toDto(Bid bid) {
        BidDto dto = new BidDto();
        dto.setId(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setTimestamp(bid.getTimestamp());
        if (bid.getBidder() != null) {
            dto.setBidderEmail(bid.getBidder().getEmail());
        }
        return dto;
    }

    public List<BidDto> toDtoList(List<Bid> bids) {
        return bids.stream().map(this::toDto).toList();
    }
}

