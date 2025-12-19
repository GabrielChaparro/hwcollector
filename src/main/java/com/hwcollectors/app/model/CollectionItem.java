package com.hwcollectors.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("collection_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItem {
    @Id private String id;
    private String userId;
    private String hotwheelId;
    private String condition;
    private LocalDate acquiredDate;
    private String notes;
}

