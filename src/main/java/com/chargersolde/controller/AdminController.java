package com.chargersolde.controller;

import com.chargersolde.dto.ApiResponse;
import com.chargersolde.dto.CreateClientRequest;
import com.chargersolde.entity.User;
import com.chargersolde.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Endpoints réservés à l'administrateur")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AuthService authService;

    // =============================================
    // POST /api/admin/clients
    // =============================================
    @PostMapping("/clients")
    @Operation(
        summary = "Créer un nouveau client",
        description = "L'admin crée un compte client. Un email avec les identifiants est envoyé automatiquement."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createClient(
            @Valid @RequestBody CreateClientRequest request) {


        User client = authService.createClient(request);

        Map<String, Object> data = Map.of(
                "id", client.getId(),
                "nom", client.getNom(),
                "prenom", client.getPrenom(),
                "email", client.getEmail(),
                "numTel", client.getNumTel(),
                "role", client.getRole().name(),
                "active", client.isActive()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Client créé avec succès. Un email a été envoyé à " + client.getEmail(),
                        data
                ));
    }
}
