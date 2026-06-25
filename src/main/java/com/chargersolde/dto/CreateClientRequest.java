package com.chargersolde.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateClientRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit avoir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Format de téléphone invalide")
    private String numTel;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères")
    private String password;
}
