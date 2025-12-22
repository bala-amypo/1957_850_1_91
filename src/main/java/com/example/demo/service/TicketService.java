package com.example.demo.service;

import com.example.demo.model.Ticket;

import java.util.List;

public interface TicketService {

    Ticket createTicket(Ticket ticket);

    Ticket getTicketById(Long id);

    List<Ticket> getAllTickets();

    Ticket updateTicket(Long id, Ticket ticket);

    void deleteTicket(Long id);
}