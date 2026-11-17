package com.campuslands.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment")
@Getter
@NoArgsConstructor
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    public EquipmentEntity(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
