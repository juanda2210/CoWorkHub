package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.service.MembershipPlanService;
import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import com.campuslands.coworkhub.persistence.repository.MemberJpaRepository;
import com.campuslands.coworkhub.persistence.repository.MembershipPlanJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanJpaRepository plans;
    private final MemberJpaRepository members;

    @Override
    @Transactional
    public MembershipPlanEntity create(MembershipPlanEntity data) {
        if (plans.existsByName(data.getName())) {
            throw new ConflictException("RN-11",
                    "Ya existe un plan de membresía con el nombre " + data.getName());
        }
        return plans.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlanEntity findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MembershipPlanEntity> findAll(Pageable pageable) {
        return plans.findAll(pageable);
    }

    @Override
    @Transactional
    public MembershipPlanEntity update(Long id, MembershipPlanEntity data) {
        MembershipPlanEntity plan = getOrThrow(id);
        if (plans.existsByNameAndIdNot(data.getName(), id)) {
            throw new ConflictException("RN-11",
                    "Ya existe un plan de membresía con el nombre " + data.getName());
        }

        plan.setName(data.getName());
        plan.setIncludedHoursPerMonth(data.getIncludedHoursPerMonth());
        plan.setOverageDiscount(data.getOverageDiscount());
        plan.setMaxActiveBookings(data.getMaxActiveBookings());
        return plan;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MembershipPlanEntity plan = getOrThrow(id);
        if (members.existsByMembershipPlanId(id)) {
            throw new ConflictException("RN-12",
                    "El plan de membresía tiene miembros asociados y no se puede eliminar");
        }
        plans.delete(plan);
    }

    private MembershipPlanEntity getOrThrow(Long id) {
        return plans.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "El plan de membresía con id " + id + " no existe"));
    }
}
