package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.model.MonthlyConsumption;
import com.campuslands.coworkhub.business.serviceResult.MemberServiceResult;
import com.campuslands.coworkhub.persistence.entity.MemberEntity;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;
import java.util.Optional;

public interface MemberService {

    MemberServiceResult<MemberEntity> register(MemberEntity member, Long membershipPlanId,
                                               String username, String password);

    Optional<MemberEntity> findByDocumentNumber(String documentNumber);

    Page<MemberEntity> findByName(String name, Pageable pageable);

    Page<MemberEntity> findByStatus(MemberStatus status, Pageable pageable);

    MemberServiceResult<MemberEntity> update(Long id, MemberEntity data, Long membershipPlanId);

    MemberServiceResult<MemberEntity> changeStatus(Long id, MemberStatus status);

    MemberServiceResult<Void> delete(Long id);

    MemberServiceResult<MonthlyConsumption> getMonthlyConsumption(Long memberId, YearMonth month);

}
