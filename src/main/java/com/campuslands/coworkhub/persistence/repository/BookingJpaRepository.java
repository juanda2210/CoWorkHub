package com.campuslands.coworkhub.persistence.repository;

import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByRoomLocationIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long locationId, LocalDateTime end, LocalDateTime start);


    List<BookingEntity> findByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId, LocalDateTime end, LocalDateTime start);

    long countByMemberIdAndStatusInAndStartTimeAfter(
            Long memberId, List<BookingStatus> statuses, LocalDateTime from);

    List<BookingEntity> findByMemberIdAndStartTimeBetween(
            Long memberId, LocalDateTime from, LocalDateTime to);

    Page<BookingEntity> findByMemberId(Long memberId, Pageable pageable);

    Page<BookingEntity> findByRoomIdAndStartTimeBetween(
            Long roomId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable);

    boolean existsByRoomId(Long roomId);

    boolean existsByMemberId(Long memberId);

    boolean existsByItemsAdditionalServiceId(Long serviceId);
}
