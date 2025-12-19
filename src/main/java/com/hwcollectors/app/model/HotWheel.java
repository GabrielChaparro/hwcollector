package com.hwcollectors.app.model;

import jakarta.persistence.*;  // ← SOLO JPA
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotwheels")
@Data
@NoArgsConstructor
@AllArgsConstructor  // ← AGREGAR
public class HotWheel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private String name, year, color, series, imageUrl;
}
