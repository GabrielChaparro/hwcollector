package com.hwcollectors.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class User {
    @Id
    private String id;
    private String keycloakId;
    private String email;
    private List<String> collectionIds = new ArrayList<>();
    private Double balance = 0.0;
}

