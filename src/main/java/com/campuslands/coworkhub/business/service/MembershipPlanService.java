package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MembershipPlanService {

    MembershipPlanEntity create(MembershipPlanEntity data);

    MembershipPlanEntity findById(Long id);

    Page<MembershipPlanEntity> findAll(Pageable pageable);

    MembershipPlanEntity update(Long id, MembershipPlanEntity data);

    void delete(Long id);
}
