package com.chargersolde.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_requests")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RechargeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private RechargeStatus status;

    @ManyToOne
    private RechargePlan plan;

    @ManyToOne
    private User client;

    private LocalDateTime createdAt;
}
