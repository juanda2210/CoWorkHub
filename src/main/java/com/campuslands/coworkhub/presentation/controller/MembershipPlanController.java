package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.MembershipPlanService;
import com.campuslands.coworkhub.presentation.dto.request.MembershipPlanRequest;
import com.campuslands.coworkhub.presentation.dto.response.MembershipPlanResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.MembershipPlanMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Planes de membresía",
        description = "Planes que contratan los miembros: horas incluidas, descuento por excedente "
                + "y reservas activas permitidas")
@RestController
@RequestMapping("/api/planes")
@AllArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService service;

    @Operation(summary = "Lista los planes de membresía",
            description = "Devuelve una página de planes. Acceso: cualquier usuario autenticado.")
    @GetMapping
    public PageResponse<MembershipPlanResponse> findAll(@ParameterObject Pageable pageable) {
        return MembershipPlanMapper.toPageResponse(service.findAll(pageable));
    }

    @Operation(summary = "Consulta un plan de membresía",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public MembershipPlanResponse findById(@PathVariable Long id) {
        return MembershipPlanMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Crea un plan de membresía",
            description = "El nombre debe ser único (RN-11) y el descuento por excedente va de 0 a 100. "
                    + "Acceso: ADMIN.")
    @ApiResponse(responseCode = "201", description = "Plan creado",
            content = @Content(schema = @Schema(implementation = MembershipPlanResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MembershipPlanResponse> create(
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse body = MembershipPlanMapper.toResponse(
                service.create(MembershipPlanMapper.toEntity(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @Operation(summary = "Actualiza un plan de membresía",
            description = "Cambia el nombre, las horas incluidas, el descuento y el máximo de reservas "
                    + "activas. Afecta a los miembros que ya tienen el plan. Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MembershipPlanResponse update(@PathVariable Long id,
                                         @Valid @RequestBody MembershipPlanRequest request) {
        return MembershipPlanMapper.toResponse(
                service.update(id, MembershipPlanMapper.toEntity(request)));
    }

    @Operation(summary = "Elimina un plan de membresía",
            description = "No se puede eliminar si tiene miembros (RN-12). Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}