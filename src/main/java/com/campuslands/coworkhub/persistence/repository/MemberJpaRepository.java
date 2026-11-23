package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.MemberEntity;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Page<MemberEntity> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    Page<MemberEntity> findByStatus(MemberStatus status, Pageable pageable);

    Page<MemberEntity> findByFullNameContainingIgnoreCaseAndStatus(
            String name, MemberStatus status, Pageable pageable);

    Optional<MemberEntity> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);

    boolean existsByMembershipPlanId(Long planId);

    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
