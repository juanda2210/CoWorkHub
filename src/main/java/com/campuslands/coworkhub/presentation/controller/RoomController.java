package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.RoomService;
import com.campuslands.coworkhub.presentation.dto.request.RoomCreateRequest;
import com.campuslands.coworkhub.presentation.dto.request.RoomUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.dto.response.RoomResponse;
import com.campuslands.coworkhub.presentation.mapper.RoomMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/salas")
@AllArgsConstructor
public class RoomController {

    private final RoomService service;

    @GetMapping
    public PageResponse<RoomResponse> findAll(@RequestParam(required = false) Long sedeId,
                                              Pageable pageable) {
        return RoomMapper.toPageResponse(sedeId == null
                ? service.findAll(pageable)
                : service.findByLocation(sedeId, pageable));
    }

    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable Long id) {
        return RoomMapper.toResponse(service.findById(id));
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable Long id,
                               @Valid @RequestBody RoomUpdateRequest request) {
        return RoomMapper.toResponse(service.update(
                id,
                RoomMapper.toEntity(request),
                RoomMapper.toEquipmentIds(request.equipmentIds())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
