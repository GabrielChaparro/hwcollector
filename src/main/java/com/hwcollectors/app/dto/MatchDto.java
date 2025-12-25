package com.hwcollectors.app.dto;

import java.math.BigDecimal;

public record MatchDto(
        String ownerAlias,
        String condition,
        String availability,
        BigDecimal askPrice
) {}

