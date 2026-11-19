package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.AdditionalServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalServiceJpaRepository extends JpaRepository<AdditionalServiceEntity, Long> {

    boolean existsByName(String name);

}
