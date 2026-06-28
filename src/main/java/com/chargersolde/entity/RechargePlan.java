package com.chargersolde.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recharge_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RechargePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label; // 1GB, 4GB, 25GB
    private Double price;
    private Integer validityDays;
    private boolean active = true; // 👈 NEW

    @JsonIgnoreProperties("plans")
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Operator operator;
}