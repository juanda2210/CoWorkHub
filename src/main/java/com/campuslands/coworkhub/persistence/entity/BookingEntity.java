package com.campuslands.coworkhub.persistence.entity;

import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int attendees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal roomCost;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal servicesCost;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(precision = 10, scale = 2)
    private BigDecimal cancellationFee;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingItemEntity> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;


    public BookingEntity(LocalDateTime startTime, LocalDateTime endTime, int attendees, BookingStatus status, BigDecimal roomCost, BigDecimal servicesCost, BigDecimal totalCost, BigDecimal cancellationFee) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendees = attendees;
        this.status = status;
        this.roomCost = roomCost;
        this.servicesCost = servicesCost;
        this.totalCost = totalCost;
        this.cancellationFee = cancellationFee;
    }
}
