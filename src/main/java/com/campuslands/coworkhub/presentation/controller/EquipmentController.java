package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.EquipmentService;
import com.campuslands.coworkhub.presentation.dto.request.EquipmentRequest;
import com.campuslands.coworkhub.presentation.dto.response.EquipmentResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.EquipmentMapper;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/equipamientos")
@AllArgsConstructor
public class EquipmentController {

    private final EquipmentService service;

    @GetMapping
    public PageResponse<EquipmentResponse> findAll(Pageable pageable) {
        return EquipmentMapper.toPageResponse(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return EquipmentMapper.toResponse(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody EquipmentRequest request) {
        EquipmentResponse body = EquipmentMapper.toResponse(
                service.create(EquipmentMapper.toEntity(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id,
                                    @Valid @RequestBody EquipmentRequest request) {
        return EquipmentMapper.toResponse(
                service.update(id, EquipmentMapper.toEntity(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}