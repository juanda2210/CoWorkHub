package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.exception.InvalidDataException;
import com.campuslands.coworkhub.business.service.BookingService;
import com.campuslands.coworkhub.persistence.enums.BookingStatus;
import com.campuslands.coworkhub.presentation.dto.request.BookingCreateRequest;
import com.campuslands.coworkhub.presentation.dto.request.BookingItemRequest;
import com.campuslands.coworkhub.presentation.dto.response.BookingResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.BookingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Reservas",
        description = "Reservas de salas, con sus servicios adicionales, costos y cancelaciones")
@RestController
@RequestMapping("/api/reservas")
@AllArgsConstructor
public class BookingController {

    private final BookingService service;

    @Operation(summary = "Crea una reserva",
            description = "Reserva una sala con sus servicios adicionales y devuelve el costo calculado. "
                    + "Reglas: dura entre 30 minutos y 8 horas, en múltiplos de 30 minutos, y empieza y "
                    + "termina el mismo día (RN-02); cae dentro del horario de la sede y se hace con al "
                    + "menos 1 hora de anticipación (RN-03); la sala está activa y no se supera su "
                    + "capacidad (RN-04); el miembro está ACTIVE (RN-05) y no supera las reservas activas "
                    + "de su plan (RN-06); no se cruza con otra reserva PENDING o CONFIRMED (RN-01). "
                    + "Acceso: ADMIN, RECEPTIONIST, o MEMBER solo a su nombre.")
    @ApiResponse(responseCode = "201", description = "Reserva creada, con su costo calculado",
            content = @Content(schema = @Schema(implementation = BookingResponse.class)))
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

    @Operation(summary = "Consulta una reserva",
            description = "Incluye el miembro, la sala, los servicios y los costos. "
                    + "Acceso: ADMIN, RECEPTIONIST y el MEMBER dueño de la reserva.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) {
        return BookingMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Lista reservas con un filtro",
            description = "Se usa uno de estos filtros: miembroId; salaId junto con fecha; o estado. "
                    + "Sin ningún filtro responde 400. Un MEMBER solo puede usar miembroId con su propio "
                    + "id. Acceso: ADMIN, RECEPTIONIST y MEMBER (solo con su propio miembroId).")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#miembroId)")
    @GetMapping
    public PageResponse<BookingResponse> list(
            @Parameter(description = "Filtro: id del miembro dueño de las reservas")
            @RequestParam(required = false) Long miembroId,
            @Parameter(description = "Filtro: id de la sala (se usa junto con fecha)")
            @RequestParam(required = false) Long salaId,
            @Parameter(description = "Filtro: día de las reservas, formato yyyy-MM-dd (se usa junto con salaId)",
                    example = "2026-11-25")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Filtro: estado de la reserva (PENDING, CONFIRMED, COMPLETED o CANCELLED)")
            @RequestParam(required = false) BookingStatus estado,
            @ParameterObject Pageable pageable) {
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

    @Operation(summary = "Agrega un servicio a una reserva",
            description = "Solo mientras la reserva está PENDING (RN-10). Si el servicio ya estaba, suma "
                    + "la cantidad. Recalcula el costo. "
                    + "Acceso: ADMIN, RECEPTIONIST y el MEMBER dueño de la reserva.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @PostMapping("/{id}/servicios")
    public BookingResponse addService(@PathVariable Long id,
                                      @Valid @RequestBody BookingItemRequest request) {
        return BookingMapper.toResponse(
                service.addService(id, request.serviceId(), request.quantity()));
    }

    @Operation(summary = "Quita un servicio de una reserva",
            description = "Solo mientras la reserva está PENDING (RN-10). Recalcula el costo y responde "
                    + "200 sin cuerpo. Acceso: ADMIN, RECEPTIONIST y el MEMBER dueño de la reserva.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @DeleteMapping("/{id}/servicios/{servicioId}")
    public void removeService(@PathVariable Long id, @PathVariable Long servicioId) {
        service.removeService(id, servicioId);
    }

    @Operation(summary = "Confirma una reserva",
            description = "Pasa la reserva de PENDING a CONFIRMED (RN-09). Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping("/{id}/confirmar")
    public BookingResponse confirm(@PathVariable Long id) {
        return BookingMapper.toResponse(service.confirm(id));
    }

    @Operation(summary = "Cancela una reserva",
            description = "Aplica RN-08: con 24 horas o más de anticipación no hay cargo; entre 24 y 2 "
                    + "horas se cobra el 50 % del costo de la sala; con menos de 2 horas, o si ya empezó, "
                    + "no se puede cancelar. Devuelve la reserva con el cargo en cancellationFee. "
                    + "Acceso: ADMIN, RECEPTIONIST y el MEMBER dueño de la reserva.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnBooking(#id)")
    @PostMapping("/{id}/cancelar")
    public BookingResponse cancel(@PathVariable Long id) {
        return BookingMapper.toResponse(service.cancel(id));
    }

    @Operation(summary = "Marca una reserva como completada",
            description = "Solo desde CONFIRMED (RN-09). Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping("/{id}/completar")
    public BookingResponse complete(@PathVariable Long id) {
        return BookingMapper.toResponse(service.complete(id));
    }
}
