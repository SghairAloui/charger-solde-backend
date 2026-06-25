package com.chargersolde.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "operators")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ooredoo, Orange, TT

    @Lob
    @Column(length = 1000000)
    private String logoUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RechargePlan> plans;
}
