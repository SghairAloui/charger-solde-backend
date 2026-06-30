package com.chargersolde.controller;

import com.chargersolde.dto.ApiResponse;
import com.chargersolde.dto.ClientBalanceDTO;
import com.chargersolde.dto.CreateClientRequest;
import com.chargersolde.entity.User;
import com.chargersolde.service.AuthService;
import com.chargersolde.service.RechargeService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;

import com.chargersolde.entity.Role;
import com.chargersolde.repository.UserRepository;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Endpoints réservés à l'administrateur")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RechargeService rechargeService;

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

    // =============================================
    // GET /api/admin/clients
    // =============================================
    @GetMapping("/clients")
    @Operation(
        summary = "Lister les clients",
        description = "Retourne la liste de tous les utilisateurs ayant le rôle CLIENT."
    )
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getClients() {
        List<User> clients =
                userRepository.findByRoleAndCreatedByAdmin(
                        Role.ROLE_CLIENT,
                        true
                );
        List<Map<String, Object>> clientsData = clients.stream().map(client -> Map.<String, Object>of(
                "id", client.getId(),
                "nom", client.getNom(),
                "prenom", client.getPrenom(),
                "email", client.getEmail(),
                "numTel", client.getNumTel(),
                "active", client.isActive(),
                "createdAt", client.getCreatedAt() != null ? client.getCreatedAt() : ""
        )).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Liste des clients récupérée", clientsData));
    }

    // =============================================
    // PATCH /api/admin/clients/{id}/status
    // =============================================
    @PatchMapping("/clients/{id}/status")
    @Operation(
        summary = "Activer / Suspendre un client",
        description = "L'admin peut activer ou suspendre un compte client."
    )
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleClientStatus(
            @PathVariable Long id) {

        User client = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        client.setActive(!client.isActive());
        userRepository.save(client);

        Map<String, Object> data = Map.of(
                "id", client.getId(),
                "nom", client.getNom(),
                "prenom", client.getPrenom(),
                "email", client.getEmail(),
                "active", client.isActive()
        );

        String statusMsg = client.isActive()
                ? "Client activé avec succès"
                : "Client suspendu avec succès";

        return ResponseEntity.ok(ApiResponse.success(statusMsg, data));
    }

    @GetMapping("/clients/balances")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientBalanceDTO> balances() {

        return rechargeService.getAllClientBalances();
    }
}
