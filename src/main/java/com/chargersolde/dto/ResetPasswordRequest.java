package com.chargersolde.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 6, max = 6, message = "Le code doit contenir 6 chiffres")
    private String code;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères")
    private String newPassword;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;
}
