package com.campuslands.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "additional_services")
@Getter
@NoArgsConstructor
public class AdditionalServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "additionalService")
    private List<BookingItemEntity> bookingItems = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public AdditionalServiceEntity(String name, BigDecimal unitPrice, List<BookingItemEntity> bookingItems) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.bookingItems = bookingItems;
    }
}
