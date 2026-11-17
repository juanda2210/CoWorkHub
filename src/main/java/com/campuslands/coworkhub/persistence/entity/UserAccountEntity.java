package com.campuslands.coworkhub.persistence.entity;

import com.campuslands.coworkhub.persistence.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts")
@Getter
@NoArgsConstructor
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    public UserAccountEntity(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
