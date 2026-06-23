package com.chargersolde.service;

import com.chargersolde.dto.ChangePasswordRequest;
import com.chargersolde.dto.ProfileResponse;
import com.chargersolde.dto.UpdateProfileRequest;
import com.chargersolde.entity.User;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public ProfileResponse getProfile(String email){


        User user =
                userRepository.findByEmail(email)
                        .orElseThrow();



        return map(user);

    }




    @Transactional
    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request){



        User user =
                userRepository.findByEmail(email)
                        .orElseThrow();



        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setNumTel(request.getNumTel());


        userRepository.save(user);



        return map(user);

    }





    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request){


        User user =
                userRepository.findByEmail(email)
                        .orElseThrow();



        if(!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        )){
            throw new IllegalArgumentException(
                    "Ancien mot de passe incorrect"
            );
        }



        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        userRepository.save(user);


    }





    private ProfileResponse map(User u){


        return ProfileResponse.builder()

                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .numTel(u.getNumTel())
                .photoUrl(u.getPhotoUrl())
                .role(u.getRole().name())

                .build();

    }



}