package com.hwcollectors.app.dto;

import java.math.BigDecimal;

public record WishlistItemDto(
        Long id,
        String hotwheelCode,
        String hotwheelName,
        int priority,
        BigDecimal maxPrice
) {}

