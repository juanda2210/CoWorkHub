package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.LocationService;
import com.campuslands.coworkhub.presentation.dto.request.LocationRequest;
import com.campuslands.coworkhub.presentation.dto.response.LocationResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.LocationMapper;
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
@RequestMapping("/api/sedes")
@AllArgsConstructor
public class LocationController {

    private final LocationService service;

    @GetMapping
    public PageResponse<LocationResponse> findAll(Pageable pageable) {
        return LocationMapper.toPageResponse(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public LocationResponse findById(@PathVariable Long id) {
        return LocationMapper.toResponse(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationRequest request) {
        LocationResponse body = LocationMapper.toResponse(
                service.create(LocationMapper.toEntity(request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public LocationResponse update(@PathVariable Long id,
                                   @Valid @RequestBody LocationRequest request) {
        return LocationMapper.toResponse(
                service.update(id, LocationMapper.toEntity(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
