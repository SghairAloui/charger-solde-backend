package com.chargersolde.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRechargeSummary {
    private Long clientId;
    private String nom;
    private String prenom;
    private String email;
    private long totalRecharges;
    private double totalAmount;
    private long pendingCount;
    private long validatedCount;
    private long rejectedCount;

    private Double balance; // ✅ nouveau

}
