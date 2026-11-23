package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.RoomService;
import com.campuslands.coworkhub.presentation.dto.response.RoomResponse;
import com.campuslands.coworkhub.presentation.mapper.RoomMapper;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/disponibilidad")
@AllArgsConstructor
public class AvailabilityController {

    private final RoomService service;

    @GetMapping
    public List<RoomResponse> findAvailable(
            @RequestParam Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) Set<Long> equipamientoIds) {
        return RoomMapper.toResponseList(service.findAvailable(
                sedeId, fecha, horaInicio, horaFin, capacidadMinima, equipamientoIds));
    }
}
