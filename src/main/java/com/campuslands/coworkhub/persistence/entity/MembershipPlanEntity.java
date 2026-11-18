package com.campuslands.coworkhub.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
public class MembershipPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, name = "included_hours_per_month")
    private int includedHoursPerMonth;

    @Column(nullable = false, name = "overage_discount", precision = 5, scale = 2)
    private BigDecimal overageDiscount;

    @Column(nullable = false, name = "max_active_bookings")
    private int maxActiveBookings;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "membershipPlan")
    private List<MemberEntity> members = new ArrayList<>();

    public MembershipPlanEntity(String name, int includedHoursPerMonth, BigDecimal overageDiscount, int maxActiveBookings) {
        this.name = name;
        this.includedHoursPerMonth = includedHoursPerMonth;
        this.overageDiscount = overageDiscount;
        this.maxActiveBookings = maxActiveBookings;
    }
}
