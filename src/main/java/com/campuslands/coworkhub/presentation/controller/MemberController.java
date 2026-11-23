package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.MemberService;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import com.campuslands.coworkhub.presentation.dto.request.MemberRegisterRequest;
import com.campuslands.coworkhub.presentation.dto.request.MemberUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.MemberResponse;
import com.campuslands.coworkhub.presentation.dto.response.MonthlyConsumptionResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.MemberMapper;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.YearMonth;

@Tag(name = "Miembros",
        description = "Clientes del coworking, con su plan, su cuenta de usuario y su consumo mensual")
@RestController
@RequestMapping("/api/miembros")
@AllArgsConstructor
public class MemberController {

    private final MemberService service;

    @Operation(summary = "Lista los miembros",
            description = "Filtros opcionales: nombre (texto contenido, sin distinguir mayúsculas) y "
                    + "estado (ACTIVE o SUSPENDED). Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping
    public PageResponse<MemberResponse> search(@RequestParam(required = false) String nombre,
                                               @RequestParam(required = false) MemberStatus estado,
                                               @ParameterObject Pageable pageable) {
        return MemberMapper.toPageResponse(service.search(nombre, estado, pageable));
    }

    @Operation(summary = "Consulta un miembro",
            description = "Un MEMBER solo puede consultar el suyo. "
                    + "Acceso: ADMIN, RECEPTIONIST y el propio MEMBER.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#id)")
    @GetMapping("/{id}")
    public MemberResponse findById(@PathVariable Long id) {
        return MemberMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Registra un miembro y su usuario",
            description = "Crea el miembro (siempre ACTIVE) y su cuenta con rol MEMBER en una sola "
                    + "operación. El documento, el email y el usuario deben ser únicos (RN-11). "
                    + "Acceso: ADMIN y RECEPTIONIST.")
    @ApiResponse(responseCode = "201", description = "Miembro registrado",
            content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        MemberResponse body = MemberMapper.toResponse(service.register(
                MemberMapper.toEntity(request),
                request.membershipPlanId(),
                request.username(),
                request.password()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @Operation(summary = "Actualiza un miembro",
            description = "Cambia el nombre, el email y el plan. El documento no se modifica. "
                    + "Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id,
                                 @Valid @RequestBody MemberUpdateRequest request) {
        return MemberMapper.toResponse(service.update(
                id,
                MemberMapper.toEntity(request),
                request.membershipPlanId()));
    }

    @Operation(summary = "Suspende un miembro",
            description = "Un miembro suspendido no puede hacer reservas (RN-05). "
                    + "Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PatchMapping("/{id}/suspender")
    public MemberResponse suspend(@PathVariable Long id) {
        return MemberMapper.toResponse(service.changeStatus(id, MemberStatus.SUSPENDED));
    }

    @Operation(summary = "Reactiva un miembro",
            description = "Devuelve al miembro al estado ACTIVE. Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PatchMapping("/{id}/activar")
    public MemberResponse activate(@PathVariable Long id) {
        return MemberMapper.toResponse(service.changeStatus(id, MemberStatus.ACTIVE));
    }

    @Operation(summary = "Elimina un miembro",
            description = "Elimina también su cuenta de usuario. No se puede eliminar si tiene "
                    + "reservas (RN-12). Acceso: ADMIN y RECEPTIONIST.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Consulta el consumo mensual de un miembro",
            description = "Muestra las horas incluidas, usadas, restantes y excedentes del mes "
                    + "indicado. No cuentan las reservas canceladas. Un MEMBER solo puede ver el suyo. "
                    + "Acceso: ADMIN, RECEPTIONIST y el propio MEMBER.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#id)")
    @GetMapping("/{id}/consumo")
    public MonthlyConsumptionResponse consumption(
            @PathVariable Long id,
            @Parameter(description = "Mes a consultar, en formato yyyy-MM", example = "2026-11")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return MemberMapper.toConsumptionResponse(service.getMonthlyConsumption(id, mes));
    }
}
