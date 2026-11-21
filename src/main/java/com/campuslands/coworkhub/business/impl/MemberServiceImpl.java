package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.component.MonthlyUsageCalculator;
import com.campuslands.coworkhub.business.model.MonthlyConsumption;
import com.campuslands.coworkhub.business.service.MemberService;
import com.campuslands.coworkhub.business.serviceResult.MemberServiceResult;
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
import java.util.Optional;

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
    public MemberServiceResult<MemberEntity> register(MemberEntity data, Long membershipPlanId,
                                                      String username, String password) {
        Optional<MembershipPlanEntity> plan = plans.findById(membershipPlanId);
        if (plan.isEmpty()) {
            return MemberServiceResult.planNotFound();
        }
        if (members.existsByDocumentNumber(data.getDocumentNumber())) {
            return MemberServiceResult.documentTaken();
        }
        if (members.existsByEmail(data.getEmail())) {
            return MemberServiceResult.emailTaken();
        }
        if (accounts.existsByUsername(username)) {
            return MemberServiceResult.usernameTaken();
        }

        data.setMembershipPlan(plan.get());
        data.setStatus(MemberStatus.ACTIVE);
        MemberEntity savedMember = members.save(data);

        UserAccountEntity account = new UserAccountEntity(
                username, passwordEncoder.encode(password), Role.MEMBER);
        account.setMember(savedMember);
        accounts.save(account);
        savedMember.setUserAccount(account);

        return MemberServiceResult.ok(savedMember);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<MemberEntity> findByDocumentNumber(String documentNumber) {
        return members.findByDocumentNumber(documentNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberEntity> findByName(String name, Pageable pageable) {
        return members.findByFullNameContainingIgnoreCase(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberEntity> findByStatus(MemberStatus status, Pageable pageable) {
        return members.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public MemberServiceResult<MemberEntity> update(Long id, MemberEntity data, Long membershipPlanId) {
        Optional<MemberEntity> current = members.findById(id);
        if (current.isEmpty()) {
            return MemberServiceResult.notFound();
        }
        Optional<MembershipPlanEntity> plan = plans.findById(membershipPlanId);
        if (plan.isEmpty()) {
            return MemberServiceResult.planNotFound();
        }
        if (members.existsByDocumentNumberAndIdNot(data.getDocumentNumber(), id)) {
            return MemberServiceResult.documentTaken();
        }
        if (members.existsByEmailAndIdNot(data.getEmail(), id)) {
            return MemberServiceResult.emailTaken();
        }

        MemberEntity member = current.get();
        member.setDocumentNumber(data.getDocumentNumber());
        member.setFullName(data.getFullName());
        member.setEmail(data.getEmail());
        member.setMembershipPlan(plan.get());
        return MemberServiceResult.ok(member);
    }

    @Override
    @Transactional
    public MemberServiceResult<MemberEntity> changeStatus(Long id, MemberStatus status) {
        Optional<MemberEntity> current = members.findById(id);
        if (current.isEmpty()) {
            return MemberServiceResult.notFound();
        }
        MemberEntity member = current.get();
        member.setStatus(status);
        return MemberServiceResult.ok(member);
    }

    @Override
    @Transactional
    public MemberServiceResult<Void> delete(Long id) {
        Optional<MemberEntity> current = members.findById(id);
        if (current.isEmpty()) {
            return MemberServiceResult.notFound();
        }
        if (bookings.existsByMemberId(id)) {
            return MemberServiceResult.hasBookings();
        }
        accounts.findByMemberId(id).ifPresent(accounts::delete);
        members.delete(current.get());
        return MemberServiceResult.ok(null);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberServiceResult<MonthlyConsumption> getMonthlyConsumption(Long memberId, YearMonth month) {
        Optional<MemberEntity> current = members.findById(memberId);
        if (current.isEmpty()) {
            return MemberServiceResult.notFound();
        }

        BigDecimal includedHours = BigDecimal.valueOf(
                current.get().getMembershipPlan().getIncludedHoursPerMonth());
        BigDecimal usedHours = usage.usedHours(memberId, month);
        BigDecimal remainingHours = includedHours.subtract(usedHours).max(BigDecimal.ZERO);
        BigDecimal overageHours = usedHours.subtract(includedHours).max(BigDecimal.ZERO);

        return MemberServiceResult.ok(
                new MonthlyConsumption(includedHours, usedHours, remainingHours, overageHours));
    }
}
