package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.MemberService;
import com.campuslands.coworkhub.persistence.enums.MemberStatus;
import com.campuslands.coworkhub.presentation.dto.request.MemberRegisterRequest;
import com.campuslands.coworkhub.presentation.dto.request.MemberUpdateRequest;
import com.campuslands.coworkhub.presentation.dto.response.MemberResponse;
import com.campuslands.coworkhub.presentation.dto.response.MonthlyConsumptionResponse;
import com.campuslands.coworkhub.presentation.dto.response.PageResponse;
import com.campuslands.coworkhub.presentation.mapper.MemberMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/miembros")
@AllArgsConstructor
public class MemberController {

    private final MemberService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping
    public PageResponse<MemberResponse> search(@RequestParam(required = false) String nombre,
                                               @RequestParam(required = false) MemberStatus estado,
                                               Pageable pageable) {
        return MemberMapper.toPageResponse(service.search(nombre, estado, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#id)")
    @GetMapping("/{id}")
    public MemberResponse findById(@PathVariable Long id) {
        return MemberMapper.toResponse(service.findById(id));
    }

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

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id,
                                 @Valid @RequestBody MemberUpdateRequest request) {
        return MemberMapper.toResponse(service.update(
                id,
                MemberMapper.toEntity(request),
                request.membershipPlanId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PatchMapping("/{id}/suspender")
    public MemberResponse suspend(@PathVariable Long id) {
        return MemberMapper.toResponse(service.changeStatus(id, MemberStatus.SUSPENDED));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PatchMapping("/{id}/activar")
    public MemberResponse activate(@PathVariable Long id) {
        return MemberMapper.toResponse(service.changeStatus(id, MemberStatus.ACTIVE));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @access.isOwnMember(#id)")
    @GetMapping("/{id}/consumo")
    public MonthlyConsumptionResponse consumption(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return MemberMapper.toConsumptionResponse(service.getMonthlyConsumption(id, mes));
    }
}
