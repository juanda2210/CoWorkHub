package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.AdditionalServiceService;
import com.campuslands.coworkhub.presentation.dto.request.AdditionalServiceRequest;
import com.campuslands.coworkhub.presentation.dto.response.AdditionalServiceResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.AdditionalServiceMapper;
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
@RequestMapping("/api/servicios-adicionales")
@AllArgsConstructor
public class AdditionalServiceController {

    private final AdditionalServiceService service;

    @GetMapping
    public PageResponse<AdditionalServiceResponse> findAll(Pageable pageable) {
        return AdditionalServiceMapper.toPageResponse(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public AdditionalServiceResponse findById(@PathVariable Long id) {
        return AdditionalServiceMapper.toResponse(service.findById(id));
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AdditionalServiceResponse update(@PathVariable Long id,
                                            @Valid @RequestBody AdditionalServiceRequest request) {
        return AdditionalServiceMapper.toResponse(
                service.update(id, AdditionalServiceMapper.toEntity(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}