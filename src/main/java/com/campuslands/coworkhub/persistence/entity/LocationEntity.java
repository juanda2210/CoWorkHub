package com.campuslands.coworkhub.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, name = "opening_time")
    private LocalTime openingTime;

    @Column(nullable = false, name = "closing_time")
    private LocalTime closingTime;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public LocationEntity(String name, String address, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
