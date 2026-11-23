package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.AdditionalServiceService;
import com.campuslands.coworkhub.presentation.dto.request.AdditionalServiceRequest;
import com.campuslands.coworkhub.presentation.dto.response.AdditionalServiceResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.AdditionalServiceMapper;
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

@Tag(name = "Servicios adicionales",
        description = "Catálogo de servicios extra que se pueden agregar a una reserva")
@RestController
@RequestMapping("/api/servicios-adicionales")
@AllArgsConstructor
public class AdditionalServiceController {

    private final AdditionalServiceService service;

    @Operation(summary = "Lista los servicios adicionales",
            description = "Devuelve una página de servicios. Acceso: cualquier usuario autenticado.")
    @GetMapping
    public PageResponse<AdditionalServiceResponse> findAll(@ParameterObject Pageable pageable) {
        return AdditionalServiceMapper.toPageResponse(service.findAll(pageable));
    }

    @Operation(summary = "Consulta un servicio adicional",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public AdditionalServiceResponse findById(@PathVariable Long id) {
        return AdditionalServiceMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Crea un servicio adicional",
            description = "El nombre debe ser único (RN-11). Acceso: ADMIN.")
    @ApiResponse(responseCode = "201", description = "Servicio creado",
            content = @Content(schema = @Schema(implementation = AdditionalServiceResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdditionalServiceResponse> create(
            @Valid @RequestBody AdditionalServiceRequest request) {
        AdditionalServiceResponse body = AdditionalServiceMapper.toResponse(
                service.create(AdditionalServiceMapper.toEntity(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @Operation(summary = "Actualiza un servicio adicional",
            description = "Cambia el nombre y el precio. Las reservas ya hechas conservan su precio. "
                    + "Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AdditionalServiceResponse update(@PathVariable Long id,
                                            @Valid @RequestBody AdditionalServiceRequest request) {
        return AdditionalServiceMapper.toResponse(
                service.update(id, AdditionalServiceMapper.toEntity(request)));
    }

    @Operation(summary = "Elimina un servicio adicional",
            description = "No se puede eliminar si está en alguna reserva (RN-12). Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}