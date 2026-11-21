package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanJpaRepository extends JpaRepository<MembershipPlanEntity, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
