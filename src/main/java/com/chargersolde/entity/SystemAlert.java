package com.chargersolde.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name="system_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlert {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;


    @Column(length = 1000)
    private String message;


    private boolean active;


    private LocalDateTime createdAt;

}
