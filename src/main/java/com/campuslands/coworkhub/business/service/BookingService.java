package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.business.serviceResult.BookingServiceResult;
import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface BookingService {

    BookingServiceResult<BookingEntity> create(Long memberId, Long roomId,
                                               LocalDateTime startTime, LocalDateTime endTime,
                                               int attendees, Map<Long, Integer> services);

    Optional<BookingEntity> findById(Long id);

    Page<BookingEntity> findByMember(Long memberId, Pageable pageable);

    Page<BookingEntity> findByRoomAndDate(Long roomId, LocalDate date, Pageable pageable);

    Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable);

    BookingServiceResult<BookingEntity> addService(Long bookingId, Long serviceId, int quantity);

    BookingServiceResult<BookingEntity> removeService(Long bookingId, Long serviceId);

    BookingServiceResult<BookingEntity> confirm(Long id);

    BookingServiceResult<BookingEntity> cancel(Long id);

    BookingServiceResult<BookingEntity> complete(Long id);
}