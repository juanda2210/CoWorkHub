package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationJpaRepository extends JpaRepository<LocationEntity, Long> {
}
