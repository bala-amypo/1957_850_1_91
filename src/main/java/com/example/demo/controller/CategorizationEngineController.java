package com.example.demo.controller;

import com.example.demo.model.CategorizationLog;
import com.example.demo.model.Ticket;
import com.example.demo.service.CategorizationEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorize")
public class CategorizationEngineController {

    private final CategorizationEngineService engineService;

    public CategorizationEngineController(CategorizationEngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping("/run/{ticketId}")
    public ResponseEntity<Ticket> runCategorization(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(engineService.categorizeTicket(ticketId));
    }

    @GetMapping("/logs/{ticketId}")
    public ResponseEntity<List<CategorizationLog>> getLogsForTicket(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(engineService.getLogsForTicket(ticketId));
    }

    @GetMapping("/log/{logId}")
    public ResponseEntity<CategorizationLog> getLog(
            @PathVariable Long logId) {

        return ResponseEntity.ok(engineService.getLogById(logId));
    }
}