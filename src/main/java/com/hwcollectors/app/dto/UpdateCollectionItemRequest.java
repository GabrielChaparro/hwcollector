package com.hwcollectors.app.dto;

import java.math.BigDecimal;

public record UpdateCollectionItemRequest(
        String availability,
        String visibility,
        BigDecimal askPrice
) {}

