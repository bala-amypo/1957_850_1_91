package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Ticket;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final TicketService ticketService;

    public CategoryController(CategoryService categoryService,
                              TicketService ticketService) {
        this.categoryService = categoryService;
        this.ticketService = ticketService;
    }

    // ---------------- CATEGORY CRUD ----------------

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    

    @PostMapping("/{categoryId}/tickets")
    public Ticket createTicket(@PathVariable Long categoryId,
                               @RequestBody Ticket ticket) {
        return ticketService.save(categoryId, ticket);
    }

    @GetMapping("/{categoryId}/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.findAll();
    }
}