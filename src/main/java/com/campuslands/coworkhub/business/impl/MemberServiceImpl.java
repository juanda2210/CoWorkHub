package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.component.MonthlyUsageCalculator;
import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.business.model.MonthlyConsumption;
import com.campuslands.coworkhub.business.service.MemberService;
import com.campuslands.coworkhub.persistence.entity.MemberEntity;
import com.campuslands.coworkhub.persistence.entity.MembershipPlanEntity;
import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import com.campuslands.coworkhub.persistence.enums.Role;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import com.campuslands.coworkhub.persistence.repository.MemberJpaRepository;
import com.campuslands.coworkhub.persistence.repository.MembershipPlanJpaRepository;
import com.campuslands.coworkhub.persistence.repository.UserAccountJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberJpaRepository members;
    private final MembershipPlanJpaRepository plans;
    private final UserAccountJpaRepository accounts;
    private final BookingJpaRepository bookings;
    private final PasswordEncoder passwordEncoder;
    private final MonthlyUsageCalculator usage;


    @Override
    @Transactional
    public MemberEntity register(MemberEntity data, Long membershipPlanId,
                                 String username, String password) {
        MembershipPlanEntity plan = getPlanOrThrow(membershipPlanId);
        if (members.existsByDocumentNumber(data.getDocumentNumber())) {
            throw new ConflictException("RN-11",
                    "Ya existe un miembro con el documento " + data.getDocumentNumber());
        }
        if (members.existsByEmail(data.getEmail())) {
            throw new ConflictException("RN-11",
                    "Ya existe un miembro con el email " + data.getEmail());
        }
        if (accounts.existsByUsername(username)) {
            throw new ConflictException("RN-11",
                    "El nombre de usuario " + username + " ya está en uso");
        }

        data.setMembershipPlan(plan);
        data.setStatus(MemberStatus.ACTIVE);
        MemberEntity savedMember = members.save(data);

        UserAccountEntity account = new UserAccountEntity(
                username, passwordEncoder.encode(password), Role.MEMBER);
        account.setMember(savedMember);
        accounts.save(account);
        savedMember.setUserAccount(account);

        return savedMember;
    }


    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByDocumentNumber(String documentNumber) {
        return members.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new NotFoundException(
                        "El miembro con documento " + documentNumber + " no existe"));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberEntity> search(String name, MemberStatus status, Pageable pageable) {
        boolean hasName = name != null && !name.isBlank();
        if (hasName && status != null) {
            return members.findByFullNameContainingIgnoreCaseAndStatus(name, status, pageable);
        }
        if (hasName) {
            return members.findByFullNameContainingIgnoreCase(name, pageable);
        }
        if (status != null) {
            return members.findByStatus(status, pageable);
        }
        return members.findAll(pageable);
    }

    @Override
    @Transactional
    public MemberEntity update(Long id, MemberEntity data, Long membershipPlanId) {
        MemberEntity member = getOrThrow(id);
        MembershipPlanEntity plan = getPlanOrThrow(membershipPlanId);
        if (members.existsByEmailAndIdNot(data.getEmail(), id)) {
            throw new ConflictException("RN-11",
                    "Ya existe un miembro con el email " + data.getEmail());
        }

        member.setFullName(data.getFullName());
        member.setEmail(data.getEmail());
        member.setMembershipPlan(plan);
        return member;
    }

    @Override
    @Transactional
    public MemberEntity changeStatus(Long id, MemberStatus status) {
        MemberEntity member = getOrThrow(id);
        member.setStatus(status);
        return member;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MemberEntity member = getOrThrow(id);
        if (bookings.existsByMemberId(id)) {
            throw new ConflictException("RN-12",
                    "El miembro tiene reservas y no se puede eliminar");
        }
        accounts.findByMemberId(id).ifPresent(accounts::delete);
        members.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyConsumption getMonthlyConsumption(Long memberId, YearMonth month) {
        MemberEntity member = getOrThrow(memberId);

        BigDecimal includedHours = BigDecimal.valueOf(
                member.getMembershipPlan().getIncludedHoursPerMonth());
        BigDecimal usedHours = usage.usedHours(memberId, month);
        BigDecimal remainingHours = includedHours.subtract(usedHours).max(BigDecimal.ZERO);
        BigDecimal overageHours = usedHours.subtract(includedHours).max(BigDecimal.ZERO);

        return new MonthlyConsumption(includedHours, usedHours, remainingHours, overageHours);
    }

    private MemberEntity getOrThrow(Long id) {
        return members.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "El miembro con id " + id + " no existe"));
    }

    private MembershipPlanEntity getPlanOrThrow(Long planId) {
        return plans.findById(planId)
                .orElseThrow(() -> new NotFoundException(
                        "El plan de membresía con id " + planId + " no existe"));
    }
}
