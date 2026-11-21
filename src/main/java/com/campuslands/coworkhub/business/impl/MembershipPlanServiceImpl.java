package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.MembershipPlanService;
import com.campuslands.coworkhub.business.serviceResult.MembershipPlanServiceResult;
import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import com.campuslands.coworkhub.persistence.repository.MemberJpaRepository;
import com.campuslands.coworkhub.persistence.repository.MembershipPlanJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanJpaRepository plans;
    private final MemberJpaRepository members;

    @Override
    @Transactional
    public MembershipPlanServiceResult<MembershipPlanEntity> create(MembershipPlanEntity data) {
        if (plans.existsByName(data.getName())) {
            return MembershipPlanServiceResult.nameTaken();
        }
        return MembershipPlanServiceResult.ok(plans.save(data));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipPlanEntity> findById(Long id) {
        return plans.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MembershipPlanEntity> findAll(Pageable pageable) {
        return plans.findAll(pageable);
    }

    @Override
    @Transactional
    public MembershipPlanServiceResult<MembershipPlanEntity> update(Long id, MembershipPlanEntity data) {
        Optional<MembershipPlanEntity> current = plans.findById(id);
        if (current.isEmpty()) {
            return MembershipPlanServiceResult.notFound();
        }
        if (plans.existsByNameAndIdNot(data.getName(), id)) {
            return MembershipPlanServiceResult.nameTaken();
        }

        MembershipPlanEntity plan = current.get();
        plan.setName(data.getName());
        plan.setIncludedHoursPerMonth(data.getIncludedHoursPerMonth());
        plan.setOverageDiscount(data.getOverageDiscount());
        plan.setMaxActiveBookings(data.getMaxActiveBookings());
        return MembershipPlanServiceResult.ok(plan);
    }

    @Override
    @Transactional
    public MembershipPlanServiceResult<Void> delete(Long id) {
        Optional<MembershipPlanEntity> current = plans.findById(id);
        if (current.isEmpty()) {
            return MembershipPlanServiceResult.notFound();
        }
        if (members.existsByMembershipPlanId(id)) {
            return MembershipPlanServiceResult.inUse();
        }
        plans.delete(current.get());
        return MembershipPlanServiceResult.ok(null);
    }
}