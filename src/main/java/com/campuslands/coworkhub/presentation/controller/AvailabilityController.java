package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.RoomService;
import com.campuslands.coworkhub.presentation.dto.response.RoomResponse;
import com.campuslands.coworkhub.presentation.mapper.RoomMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Disponibilidad",
        description = "Consulta de las salas libres de una sede en un día y un rango horario")
@RestController
@RequestMapping("/api/disponibilidad")
@AllArgsConstructor
public class AvailabilityController {

    private final RoomService service;

    @Operation(summary = "Busca las salas libres",
            description = "Devuelve las salas activas de la sede que no tienen una reserva PENDING o "
                    + "CONFIRMED que se cruce con el rango pedido. Si una reserva termina justo cuando "
                    + "empieza el rango, no se cruzan. Filtros opcionales: capacidad mínima y equipamiento "
                    + "(la sala debe tener todo el equipamiento indicado). "
                    + "Acceso: cualquier usuario autenticado.")
    @GetMapping
    public List<RoomResponse> findAvailable(
            @Parameter(description = "Id de la sede")
            @RequestParam Long sedeId,
            @Parameter(description = "Día a consultar, formato yyyy-MM-dd", example = "2026-11-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Hora de inicio, formato HH:mm", example = "09:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @Parameter(description = "Hora de fin, formato HH:mm", example = "11:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @Parameter(description = "Capacidad mínima de la sala (opcional)")
            @RequestParam(required = false) Integer capacidadMinima,
            @Parameter(description = "Ids del equipamiento que debe tener la sala, separados por coma (opcional)",
                    example = "1,2")
            @RequestParam(required = false) Set<Long> equipamientoIds) {
        return RoomMapper.toResponseList(service.findAvailable(
                sedeId, fecha, horaInicio, horaFin, capacidadMinima, equipamientoIds));
    }
}
