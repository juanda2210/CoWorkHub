package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.RoomService;
import com.campuslands.coworkhub.presentation.dto.request.RoomCreateRequest;
import com.campuslands.coworkhub.presentation.dto.request.RoomUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.dto.response.RoomResponse;
import com.campuslands.coworkhub.presentation.mapper.RoomMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Salas",
        description = "Salas de cada sede, con su equipamiento")
@RestController
@RequestMapping("/api/salas")
@AllArgsConstructor
public class RoomController {

    private final RoomService service;

    @Operation(summary = "Lista las salas",
            description = "Devuelve una página de salas. Con sedeId, solo las de esa sede. "
                    + "Acceso: cualquier usuario autenticado.")
    @GetMapping
    public PageResponse<RoomResponse> findAll(@RequestParam(required = false) Long sedeId,
                                              @ParameterObject Pageable pageable) {
        return RoomMapper.toPageResponse(sedeId == null
                ? service.findAll(pageable)
                : service.findByLocation(sedeId, pageable));
    }

    @Operation(summary = "Consulta una sala",
            description = "Incluye su sede y su equipamiento. Acceso: cualquier usuario autenticado.")
    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable Long id) {
        return RoomMapper.toResponse(service.findById(id));
    }

    @Operation(summary = "Crea una sala",
            description = "El nombre debe ser único dentro de la sede (RN-11). Se indica la sede y los "
                    + "ids del equipamiento. Acceso: ADMIN.")
    @ApiResponse(responseCode = "201", description = "Sala creada",
            content = @Content(schema = @Schema(implementation = RoomResponse.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomCreateRequest request) {
        RoomResponse body = RoomMapper.toResponse(service.create(
                RoomMapper.toEntity(request),
                request.locationId(),
                RoomMapper.toEquipmentIds(request.equipmentIds())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @Operation(summary = "Actualiza una sala",
            description = "Una sala no cambia de sede. Reemplaza su equipamiento por el indicado. "
                    + "Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable Long id,
                               @Valid @RequestBody RoomUpdateRequest request) {
        return RoomMapper.toResponse(service.update(
                id,
                RoomMapper.toEntity(request),
                RoomMapper.toEquipmentIds(request.equipmentIds())));
    }

    @Operation(summary = "Elimina una sala",
            description = "No se puede eliminar si tiene reservas (RN-12). Acceso: ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
