package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, Long> {

    Optional<UserAccountEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserAccountEntity> findByMemberId(Long memberId);
}
