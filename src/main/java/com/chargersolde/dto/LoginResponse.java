package com.chargersolde.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type;
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String photoUrl;

    public static LoginResponse of(String token, Long id, String nom,
                                    String prenom, String email, String role, String photoUrl) {
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .id(id)
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .role(role)
                .photoUrl(photoUrl)
                .build();
    }
}
