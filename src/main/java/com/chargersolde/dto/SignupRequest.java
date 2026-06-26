package com.chargersolde.dto;


import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class SignupRequest {


    @NotBlank
    private String nom;


    @NotBlank
    private String prenom;


    @Email
    @NotBlank
    private String email;


    @NotBlank
    private String numTel;


    @NotBlank
    @Size(min=8)
    private String password;


}