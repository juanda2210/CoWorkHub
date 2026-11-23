package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.model.MonthlyConsumption;
import com.campuslands.coworkhub.persistence.entity.MemberEntity;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface MemberService {

    MemberEntity register(MemberEntity member, Long membershipPlanId,
                          String username, String password);

    MemberEntity findById(Long id);

    MemberEntity findByDocumentNumber(String documentNumber);

    Page<MemberEntity> search(String name, MemberStatus status, Pageable pageable);

    MemberEntity update(Long id, MemberEntity data, Long membershipPlanId);

    MemberEntity changeStatus(Long id, MemberStatus status);

    void delete(Long id);

    MonthlyConsumption getMonthlyConsumption(Long memberId, YearMonth month);

}
