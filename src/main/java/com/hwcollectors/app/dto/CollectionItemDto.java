package com.hwcollectors.app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionItemDto {
    private Long id;
    private String hotwheelCode;
    private String hotwheelName;
    private String condition;
    private LocalDate acquiredDate;
}
