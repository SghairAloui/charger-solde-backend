package com.chargersolde.repository;

import com.chargersolde.entity.ClientBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClientBalanceHistoryRepository
        extends JpaRepository<ClientBalanceHistory,Long> {



    List<ClientBalanceHistory>
    findByClientIdAndDateBetween(
            Long clientId,
            LocalDate start,
            LocalDate end
    );



    @Query("""
SELECT SUM(c.totalAmount)
FROM ClientBalanceHistory c
WHERE c.client.id=:clientId
AND c.date BETWEEN :start AND :end
AND c.paid=false
""")
    Double calculateBalance(
            Long clientId,
            LocalDate start,
            LocalDate end
    );



}
