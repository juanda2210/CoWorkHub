package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.serviceResult.MembershipPlanServiceResult;
import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MembershipPlanService {

    MembershipPlanServiceResult<MembershipPlanEntity> create(MembershipPlanEntity data);

    Optional<MembershipPlanEntity> findById(Long id);

    Page<MembershipPlanEntity> findAll(Pageable pageable);

    MembershipPlanServiceResult<MembershipPlanEntity> update(Long id, MembershipPlanEntity data);

    MembershipPlanServiceResult<Void> delete(Long id);
}