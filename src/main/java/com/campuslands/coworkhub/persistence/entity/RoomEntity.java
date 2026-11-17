package com.campuslands.coworkhub.persistence.entity;


import com.campuslands.coworkhub.persistence.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public RoomEntity(String name, RoomType type, int capacity, BigDecimal hourlyRate, boolean active) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }
}
