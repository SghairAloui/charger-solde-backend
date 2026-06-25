package com.chargersolde.controller;

import com.chargersolde.dto.ApiResponse;
import com.chargersolde.dto.ClaimDTO;
import com.chargersolde.dto.CreateClaimRequest;
import com.chargersolde.entity.Claim.ClaimStatus;
import com.chargersolde.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@Tag(name = "Réclamations", description = "Gestion des réclamations clients")
@SecurityRequirement(name = "BearerAuth")
public class ClaimController {

    private final ClaimService claimService;

    // ── CLIENT: Create claim ──────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Créer une réclamation")
    public ResponseEntity<ApiResponse<ClaimDTO>> createClaim(
            Authentication auth,
            @Valid @RequestBody CreateClaimRequest request) {

        ClaimDTO dto = claimService.createClaim(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Réclamation soumise avec succès", dto));
    }

    // ── CLIENT: Get my claims ─────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Mes réclamations")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getMyClaims(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Réclamations récupérées",
                claimService.getMyClaims(auth.getName())));
    }

    // ── ADMIN: Get all claims ─────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toutes les réclamations (Admin)")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getAllClaims() {
        return ResponseEntity.ok(ApiResponse.success("Liste des réclamations",
                claimService.getAllClaims()));
    }

    // ── ADMIN: Update status + response ───────────────────────────
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour le statut d'une réclamation")
    public ResponseEntity<ApiResponse<ClaimDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam ClaimStatus status,
            @RequestBody(required = false) Map<String, String> body) {

        String response = (body != null) ? body.get("response") : null;
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour",
                claimService.updateStatus(id, status, response)));
    }
}
