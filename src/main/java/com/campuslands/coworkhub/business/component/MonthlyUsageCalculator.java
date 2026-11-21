package com.campuslands.coworkhub.business.component;

import com.campuslands.coworkhub.persistence.entity.BookingEntity;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import com.campuslands.coworkhub.persistence.repository.BookingJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Component
@AllArgsConstructor
public class MonthlyUsageCalculator {

    private static final BigDecimal MINUTES_PER_HOUR = BigDecimal.valueOf(60);

    private final BookingJpaRepository bookings;

    public BigDecimal toHours(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Duration.between(start, end).toMinutes())
                .divide(MINUTES_PER_HOUR, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal usedHours(Long memberId, YearMonth month) {
        List<BookingEntity> monthBookings = bookings.findByMemberIdAndStartTimeBetween(
                memberId, month.atDay(1).atStartOfDay(), month.atEndOfMonth().atTime(LocalTime.MAX));

        BigDecimal total = BigDecimal.ZERO;
        for (BookingEntity booking : monthBookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                total = total.add(toHours(booking.getStartTime(), booking.getEndTime()));
            }
        }
        return total;
    }
}