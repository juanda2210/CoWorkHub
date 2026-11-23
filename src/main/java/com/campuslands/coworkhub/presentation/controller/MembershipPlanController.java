package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.MembershipPlanService;
import com.campuslands.coworkhub.presentation.dto.request.MembershipPlanRequest;
import com.campuslands.coworkhub.presentation.dto.response.MembershipPlanResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.MembershipPlanMapper;
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
@RequestMapping("/api/planes")
@AllArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService service;

    @GetMapping
    public PageResponse<MembershipPlanResponse> findAll(Pageable pageable) {
        return MembershipPlanMapper.toPageResponse(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public MembershipPlanResponse findById(@PathVariable Long id) {
        return MembershipPlanMapper.toResponse(service.findById(id));
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MembershipPlanResponse update(@PathVariable Long id,
                                         @Valid @RequestBody MembershipPlanRequest request) {
        return MembershipPlanMapper.toResponse(
                service.update(id, MembershipPlanMapper.toEntity(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}