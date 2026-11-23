package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.exception.InvalidDataException;
import com.campuslands.coworkhub.business.service.BookingService;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import com.campuslands.coworkhub.presentation.dto.request.BookingCreateRequest;
import com.campuslands.coworkhub.presentation.dto.request.BookingItemRequest;
import com.campuslands.coworkhub.presentation.dto.response.BookingResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.BookingMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reservas")
@AllArgsConstructor
public class BookingController {

    private final BookingService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#request.memberId())")
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingCreateRequest request) {
        BookingResponse body = BookingMapper.toResponse(service.create(
                request.memberId(),
                request.roomId(),
                request.startTime(),
                request.endTime(),
                request.attendees(),
                BookingMapper.toServicesMap(request.services())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) {
        return BookingMapper.toResponse(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#miembroId)")
    @GetMapping
    public PageResponse<BookingResponse> list(
            @RequestParam(required = false) Long miembroId,
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) BookingStatus estado,
            Pageable pageable) {
        if (miembroId != null) {
            return BookingMapper.toPageResponse(service.findByMember(miembroId, pageable));
        }
        if (salaId != null && fecha != null) {
            return BookingMapper.toPageResponse(service.findByRoomAndDate(salaId, fecha, pageable));
        }
        if (estado != null) {
            return BookingMapper.toPageResponse(service.findByStatus(estado, pageable));
        }
        throw new InvalidDataException(
                "Indique un filtro: miembroId, salaId junto con fecha, o estado");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @PostMapping("/{id}/servicios")
    public BookingResponse addService(@PathVariable Long id,
                                      @Valid @RequestBody BookingItemRequest request) {
        return BookingMapper.toResponse(
                service.addService(id, request.serviceId(), request.quantity()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @DeleteMapping("/{id}/servicios/{servicioId}")
    public void removeService(@PathVariable Long id, @PathVariable Long servicioId) {
        service.removeService(id, servicioId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping("/{id}/confirmar")
    public BookingResponse confirm(@PathVariable Long id) {
        return BookingMapper.toResponse(service.confirm(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @PostMapping("/{id}/cancelar")
    public BookingResponse cancel(@PathVariable Long id) {
        return BookingMapper.toResponse(service.cancel(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping("/{id}/completar")
    public BookingResponse complete(@PathVariable Long id) {
        return BookingMapper.toResponse(service.complete(id));
    }
}
