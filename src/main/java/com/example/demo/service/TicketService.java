package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.model.Ticket;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepo;
    private final CategoryRepository categoryRepo;

    public TicketService(TicketRepository ticketRepo, CategoryRepository categoryRepo) {
        this.ticketRepo = ticketRepo;
        this.categoryRepo = categoryRepo;
    }

    public Ticket save(Long categoryId, Ticket ticket) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        ticket.setCategory(category);
        return ticketRepo.save(ticket);
    }

    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    public Ticket findById(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }
}