package com.campuslands.coworkhub.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plans")
@Getter
@NoArgsConstructor
public class MembershipPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "included_hours_per_month")
    private int includedHoursPerMonth;

    @Column(nullable = false, name = "overage_discount")
    private BigDecimal overageDiscount;

    @Column(nullable = false, name = "max_active_bookings")
    private int maxActiveBookings;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public MembershipPlanEntity(String name, int includedHoursPerMonth, BigDecimal overageDiscount, int maxActiveBookings) {
        this.name = name;
        this.includedHoursPerMonth = includedHoursPerMonth;
        this.overageDiscount = overageDiscount;
        this.maxActiveBookings = maxActiveBookings;
    }
}
