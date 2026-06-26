package com.chargersolde.service;

import com.chargersolde.dto.*;
import com.chargersolde.entity.Role;
import com.chargersolde.entity.User;
import com.chargersolde.exception.EmailAlreadyExistsException;
import com.chargersolde.exception.InvalidTokenException;
import com.chargersolde.repository.UserRepository;
import com.chargersolde.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chargersolde.entity.AccountStatus;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Value("${app.jwt.reset-password-expiration}")
    private long resetPasswordExpiration;

    // =============================================
    // LOGIN
    // =============================================
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            if(
                    user.getAccountStatus()
                            != AccountStatus.APPROVED
            ){

                throw new BadCredentialsException(
                        "Compte non validé par l'administrateur"
                );

            }
            String jwt = jwtUtil.generateToken(user);

            log.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());

            return LoginResponse.of(
                    jwt,
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getPhotoUrl()
            );

        } catch (BadCredentialsException e) {
            log.warn("Tentative de connexion échouée pour: {}", request.getEmail());
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
    }

    // =============================================
    // CRÉER UN CLIENT (par l'admin)
    // =============================================
    @Transactional
    public User createClient(CreateClientRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Un compte avec l'email " + request.getEmail() + " existe déjà"
            );
        }

        String rawPassword = request.getPassword();

        User client = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .numTel(request.getNumTel())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ROLE_CLIENT)
                .active(true)
                .accountStatus(AccountStatus.APPROVED)
                .build();

        User savedClient = userRepository.save(client);
        log.info("Nouveau client créé: {}", savedClient.getEmail());

        // Envoyer email avec credentials
        try {
            emailService.sendCredentialsEmail(
                    savedClient.getEmail(),
                    savedClient.getNom(),
                    savedClient.getPrenom(),
                    savedClient.getEmail(),
                    rawPassword  // mot de passe en clair pour l'email uniquement
            );
        } catch (Exception e) {
            log.error("Erreur envoi email pour {}: {}", savedClient.getEmail(), e.getMessage());
            // On ne bloque pas la création si l'email échoue
        }

        return savedClient;
    }

    // =============================================
    // MOT DE PASSE OUBLIÉ
    // =============================================
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun compte associé à cet email: " + request.getEmail()
                ));

        // Générer un code à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(999999));
        long expirationMs = resetPasswordExpiration;
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(expirationMs / 1000);

        user.setResetPasswordToken(code);
        user.setResetPasswordTokenExpiry(expiry);
        userRepository.save(user);

        // Envoyer l'email avec le code
        emailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getNom(),
                code
        );

        log.info("Email de reset password (code) envoyé à: {}", user.getEmail());
    }

    // =============================================
    // RÉINITIALISER LE MOT DE PASSE
    // =============================================
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Valider la correspondance des mots de passe
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Trouver l'utilisateur par email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun compte associé à cet email: " + request.getEmail()
                ));

        // Vérifier le code
        if (user.getResetPasswordToken() == null ||
            !user.getResetPasswordToken().equals(request.getCode())) {
            throw new InvalidTokenException("Code invalide ou inexistant");
        }

        // Vérifier l'expiration du code
        if (user.getResetPasswordTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getResetPasswordTokenExpiry())) {
            throw new InvalidTokenException("Le code a expiré. Veuillez refaire une demande.");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        log.info("Mot de passe réinitialisé pour: {}", user.getEmail());
    }

    // =============================================
    // VALIDER LE CODE DE RÉINITIALISATION
    // =============================================
    public void validateResetCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun compte associé à cet email: " + email
                ));

        if (user.getResetPasswordToken() == null ||
            !user.getResetPasswordToken().equals(code)) {
            throw new InvalidTokenException("Code invalide");
        }

        if (user.getResetPasswordTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getResetPasswordTokenExpiry())) {
            throw new InvalidTokenException("Le code a expiré. Veuillez refaire une demande.");
        }
    }

    @Transactional
    public void signup(
            SignupRequest request
    ){


        if(userRepository.existsByEmail(request.getEmail())){

            throw new EmailAlreadyExistsException(
                    "Email existe déjà"
            );

        }



        User user =
                User.builder()

                        .nom(request.getNom())
                        .prenom(request.getPrenom())
                        .email(request.getEmail())
                        .numTel(request.getNumTel())

                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )

                        .role(Role.ROLE_CLIENT)

                        .active(false)

                        .accountStatus(AccountStatus.PENDING)

                        .build();



        userRepository.save(user);



        log.info(
                "Nouvelle demande inscription {}",
                user.getEmail()
        );



    }
}
