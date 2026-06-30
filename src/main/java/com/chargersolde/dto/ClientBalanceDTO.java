package com.chargersolde.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientBalanceDTO {

    private Long clientId;
    private String email;
    private Double balance;
}