package com.campuslands.coworkhub.persistence.entity;


import com.campuslands.coworkhub.persistence.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_location_name",
                columnNames = {"location_id", "name"}
        ))
@Getter
@Setter
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToMany
    @JoinTable(
            name = "room_equipment",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private Set<EquipmentEntity> equipment = new HashSet<>();

    @OneToMany(mappedBy = "room")
    private List<BookingEntity> bookings = new ArrayList<>();

    public RoomEntity(String name, RoomType type, int capacity, BigDecimal hourlyRate, boolean active, LocationEntity location) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
        this.active = active;
        this.location = location;
    }
}
