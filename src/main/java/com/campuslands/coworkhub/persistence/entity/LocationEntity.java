package com.campuslands.coworkhub.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, name = "opening_time")
    private LocalTime openingTime;

    @Column(nullable = false, name = "closing_time")
    private LocalTime closingTime;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "location")
    private List<RoomEntity> rooms = new ArrayList<>();

    public LocationEntity(String name, String city, String address, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
