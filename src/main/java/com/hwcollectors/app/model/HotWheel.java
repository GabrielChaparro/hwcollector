package com.hwcollectors.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class HotWheel {
    @Id
    private String id;
    private String name, year, color, series, imageUrl;
    private String code;
}

