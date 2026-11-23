package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface BookingService {

    BookingEntity create(Long memberId, Long roomId,
                         LocalDateTime startTime, LocalDateTime endTime,
                         int attendees, Map<Long, Integer> services);

    BookingEntity findById(Long id);

    Page<BookingEntity> findByMember(Long memberId, Pageable pageable);

    Page<BookingEntity> findByRoomAndDate(Long roomId, LocalDate date, Pageable pageable);

    Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable);

    BookingEntity addService(Long bookingId, Long serviceId, int quantity);

    BookingEntity removeService(Long bookingId, Long serviceId);

    BookingEntity confirm(Long id);

    BookingEntity cancel(Long id);

    BookingEntity complete(Long id);
}
