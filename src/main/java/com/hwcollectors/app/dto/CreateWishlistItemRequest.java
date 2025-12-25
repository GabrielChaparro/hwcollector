package com.hwcollectors.app.dto;

import java.math.BigDecimal;

public record CreateWishlistItemRequest(
        String hotwheelCode,
        Integer priority,
        BigDecimal maxPrice
) {}

