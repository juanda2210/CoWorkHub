package com.campuslands.coworkhub.persistence.entity;

import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "document_number")
    private String documentNumber;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlanEntity membershipPlan;

    @OneToOne(mappedBy = "member")
    private UserAccountEntity userAccount;

    @OneToMany(mappedBy = "member")
    private List<BookingEntity> bookings = new ArrayList<>();

    public MemberEntity(String documentNumber, String fullName, String email, MemberStatus status) {
        this.documentNumber = documentNumber;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
    }
}
