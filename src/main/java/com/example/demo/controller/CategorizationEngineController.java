package com.example.demo.controller;

import com.example.demo.model.Ticket;
import com.example.demo.model.CategorizationLog;
import com.example.demo.service.CategorizationEngineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorize")
@Tag(name = "Engine", description = "Categorization engine endpoints")
public class CategorizationEngineController {
    
    private final CategorizationEngineService engineService;
    
    public CategorizationEngineController(CategorizationEngineService engineService) {
        this.engineService = engineService;
    }
    
    @PostMapping("/run/{ticketId}")
    public ResponseEntity<Ticket> categorizeTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(engineService.categorizeTicket(ticketId));
    }
    
    @GetMapping("/logs/{ticketId}")
    public ResponseEntity<List<CategorizationLog>> getLogsForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(engineService.getLogsForTicket(ticketId));
    }
    
    @GetMapping("/log/{id}")
    public ResponseEntity<CategorizationLog> getLog(@PathVariable Long id) {
        return ResponseEntity.ok(engineService.getLog(id));
    }
}