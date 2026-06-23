package com.chargersolde.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {


    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String numTel;

    private String photoUrl;

    private String role;


}