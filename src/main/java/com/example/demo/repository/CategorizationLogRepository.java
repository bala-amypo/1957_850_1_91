package com.example.demo.repository;

import com.example.demo.model.CategorizationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategorizationLogRepository extends JpaRepository<CategorizationLog, Long> {
    List<CategorizationLog> findByTicketId(Long ticketId);
    List<CategorizationLog> findByTicket_Id(Long ticketId);
}