package com.campuslands.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_items")
@Getter
@NoArgsConstructor
public class BookingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedUnitPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "additional_service_id", nullable = false)
    private AdditionalServiceEntity additionalService;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public BookingItemEntity(int quantity, BigDecimal appliedUnitPrice, BookingEntity booking, AdditionalServiceEntity additionalService) {
        this.quantity = quantity;
        this.appliedUnitPrice = appliedUnitPrice;
        this.booking = booking;
        this.additionalService = additionalService;
    }
}
