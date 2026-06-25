package com.chargersolde.controller;

import com.chargersolde.dto.*;
import com.chargersolde.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour la connexion et la gestion des mots de passe")
public class AuthController {

    private final AuthService authService;

    // =============================================
    // POST /api/auth/login
    // =============================================
    @PostMapping("/login")
    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur (ADMIN ou CLIENT) et retourne un token JWT"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Connexion réussie, token JWT retourné"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Email ou mot de passe incorrect"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Connexion réussie", response)
        );
    }

    // =============================================
    // POST /api/auth/forgot-password
    // =============================================
    @PostMapping("/forgot-password")
    @Operation(
        summary = "Mot de passe oublié",
        description = "Envoie un email de réinitialisation du mot de passe au client"
    )
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("Un email de réinitialisation a été envoyé à " + request.getEmail())
        );
    }

    // =============================================
    // POST /api/auth/reset-password
    // =============================================
    @PostMapping("/reset-password")
    @Operation(
        summary = "Réinitialiser le mot de passe",
        description = "Réinitialise le mot de passe avec le token reçu par email (valable 1 heure)"
    )
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success("Mot de passe réinitialisé avec succès")
        );
    }

    // =============================================
    // POST /api/auth/validate-reset-code
    // =============================================
    @PostMapping("/validate-reset-code")
    @Operation(
        summary = "Valider un code de réinitialisation",
        description = "Vérifie si le code de réinitialisation est valide et non expiré pour l'email donné"
    )
    public ResponseEntity<ApiResponse<Void>> validateResetCode(
            @RequestParam String email,
            @RequestParam String code) {
        authService.validateResetCode(email, code);
        return ResponseEntity.ok(ApiResponse.success("Code valide"));
    }
}
