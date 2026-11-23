package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.EquipmentService;
import com.campuslands.coworkhub.presentation.dto.request.EquipmentRequest;
import com.campuslands.coworkhub.presentation.dto.response.EquipmentResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.EquipmentMapper;
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

@Tag(name = "Equipamientos",
        description = "Catálogo del equipamiento que pueden tener las salas (proyector, pizarra, etc.)")
@RestController
@RequestMapping("/api/equipamientos")
@AllArgsConstructor
public class EquipmentController {

    private final EquipmentService service;

    @Operation(summary = "Lista el equipamiento",
            description = "Devuelve una página de equipamiento. Acceso: cualquier usuario autenticado.")
    @GetMapping
    public PageResponse<EquipmentResponse> findAll(@ParameterObject Pageable pageable) {
        return EquipmentMapper.toPageResponse(service.findAll(pageable));
    }

    @Operation(summary = "Consulta un equipamiento",
            description = "Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return EquipmentMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Crea un equipamiento",
            description = "El nombre debe ser único (RN-11). Acceso: ADMIN.")
    @ApiResponse(responseCode = "201", description = "Equipamiento creado",
            content = @Content(schema = @Schema(implementation = EquipmentResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        EquipmentResponse body = EquipmentMapper.toResponse(
                service.create(EquipmentMapper.toEntity(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @Operation(summary = "Actualiza un equipamiento",
            description = "Cambia el nombre y la cantidad. Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id,
                                    @Valid @RequestBody EquipmentRequest request) {
        return EquipmentMapper.toResponse(
                service.update(id, EquipmentMapper.toEntity(request)));
    }

    @Operation(summary = "Elimina un equipamiento",
            description = "No se puede eliminar si alguna sala lo usa (RN-12). Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}