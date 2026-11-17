package com.campuslands.coworkhub.persistence.entity;

import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
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
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public MemberEntity(String documentNumber, String fullName, String email, MemberStatus status) {
        this.documentNumber = documentNumber;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
    }
}
