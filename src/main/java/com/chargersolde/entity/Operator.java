package com.chargersolde.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "operators")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ooredoo, Orange, TT

    private String logoUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "operator")
    private List<RechargePlan> plans;
}
