package com.chargersolde.controller;


import com.chargersolde.entity.AccountStatus;
import com.chargersolde.entity.User;
import com.chargersolde.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminClientController {



    private final UserRepository userRepository;



    /**
     * Voir les demandes inscription
     */
    @GetMapping("/pending")
    public ResponseEntity<List<User>> pendingClients(){


        return ResponseEntity.ok(

                userRepository.findByAccountStatus(
                        AccountStatus.PENDING
                )

        );

    }





    /**
     * Accepter un client
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveClient(
            @PathVariable Long id
    ){



        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client introuvable"
                                )
                        );



        user.setAccountStatus(
                AccountStatus.APPROVED
        );


        user.setActive(true);



        userRepository.save(user);



        return ResponseEntity.ok(

                Map.of(
                        "message",
                        "Compte client accepté"
                )

        );

    }







    /**
     * Refuser un client
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectClient(
            @PathVariable Long id
    ){



        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client introuvable"
                                )
                        );




        user.setAccountStatus(
                AccountStatus.REJECTED
        );


        user.setActive(false);



        userRepository.save(user);



        return ResponseEntity.ok(

                Map.of(
                        "message",
                        "Compte client refusé"
                )

        );

    }



}