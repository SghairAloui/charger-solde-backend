package com.chargersolde.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name="client_balance_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientBalanceHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private User client;


    private LocalDate date;


    private Double totalAmount;


    private Integer validatedOrders;


    private boolean paid;

}