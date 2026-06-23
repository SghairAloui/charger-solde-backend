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



@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(
        name = "Profile",
        description = "Gestion du profil utilisateur"
)
public class ProfileController {



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
    @PostMapping(
            value = "/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadPhoto(
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {



        if(file.isEmpty()){
            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Veuillez sélectionner une image"
                            )
                    );
        }



        // Taille max 2 MB
        if(file.getSize() > 2 * 1024 * 1024){

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Image trop grande (max 2MB)"
                            )
                    );
        }



        String email =
                authentication.getName();



        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Utilisateur introuvable"
                                )
                        );




        String extension =
                getExtension(
                        file.getOriginalFilename()
                );



        String filename =
                UUID.randomUUID()
                        + extension;



        Path uploadPath =
                Paths.get(
                        "uploads/profile"
                );



        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }



        Path filePath =
                uploadPath.resolve(filename);



        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );




        user.setPhotoUrl(
                "/uploads/profile/"
                        + filename
        );


        userRepository.save(user);




        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Photo modifiée avec succès",

                        "photoUrl",
                        user.getPhotoUrl()
                )
        );


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