package com.chargersolde.controller;


import com.chargersolde.dto.ChangePasswordRequest;
import com.chargersolde.dto.ProfileResponse;
import com.chargersolde.dto.UpdateProfileRequest;
import com.chargersolde.entity.User;
import com.chargersolde.repository.UserRepository;
import com.chargersolde.service.ProfileService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(
        name = "Profile",
        description = "Gestion du profil utilisateur"
)
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Value("${app.upload.dir:uploads/profile}")
    private String uploadDir;


    private final ProfileService profileService;

    private final UserRepository userRepository;



    /**
     * ==============================
     * GET MY PROFILE
     * ==============================
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication
    ){

        String email = authentication.getName();


        return ResponseEntity.ok(
                profileService.getProfile(email)
        );
    }





    /**
     * ==============================
     * UPDATE PROFILE
     * ==============================
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ){

        String email = authentication.getName();


        return ResponseEntity.ok(
                profileService.updateProfile(
                        email,
                        request
                )
        );
    }





    /**
     * ==============================
     * CHANGE PASSWORD
     * ==============================
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ){

        String email = authentication.getName();


        profileService.changePassword(
                email,
                request
        );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Mot de passe modifié avec succès"
                )
        );
    }





    /**
     * ==============================
     * UPLOAD PHOTO PROFILE
     * ==============================
     */

    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {

        log.info("=== Upload photo démarré ===");
        log.info("Fichier reçu: {} ({} octets)", file.getOriginalFilename(), file.getSize());

        try {

        if(file.isEmpty()){
            log.warn("Fichier vide reçu");
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Veuillez sélectionner une image"));
        }

        if(file.getSize() > 5 * 1024 * 1024){
            log.warn("Fichier trop gros: {} octets", file.getSize());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Image trop grande (max 5MB)"));
        }

        String email = authentication.getName();
        log.info("Upload pour utilisateur: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        log.info("Chemin d'upload: {}", uploadPath);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
            log.info("Répertoire créé: {}", uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String photoUrl = "/uploads/profile/" + filename;
        user.setPhotoUrl(photoUrl);
        userRepository.save(user);

        log.info("Photo uploadée avec succès: {} -> {}", email, photoUrl);

        return ResponseEntity.ok(Map.of(
                "message", "Photo modifiée avec succès",
                "photoUrl", photoUrl
        ));

        } catch (Exception e) {
            log.error("ERREUR upload photo: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }





    /**
     * ==============================
     * DELETE PHOTO
     * ==============================
     */
    @DeleteMapping("/photo")
    public ResponseEntity<?> deletePhoto(
            Authentication authentication
    ) throws IOException {



        User user =
                userRepository.findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow();



        if(user.getPhotoUrl()!=null){


            Path path =
                    Paths.get(
                            "." +
                                    user.getPhotoUrl()
                    );


            Files.deleteIfExists(path);


            user.setPhotoUrl(null);

            userRepository.save(user);

        }



        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Photo supprimée"
                )
        );

    }

    private String getExtension(String filename){


        if(filename==null){
            return ".png";
        }


        int index =
                filename.lastIndexOf(".");


        if(index==-1){
            return ".png";
        }


        return filename.substring(index);

    }


}