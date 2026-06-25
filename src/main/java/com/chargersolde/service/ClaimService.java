package com.chargersolde.service;

import com.chargersolde.dto.ClaimDTO;
import com.chargersolde.dto.CreateClaimRequest;
import com.chargersolde.entity.Claim;
import com.chargersolde.entity.Claim.ClaimStatus;
import com.chargersolde.entity.User;
import com.chargersolde.repository.ClaimRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    // ─── CLIENT: Create a claim ───────────────────────────────────
    @Transactional
    public ClaimDTO createClaim(String email, CreateClaimRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        Claim claim = Claim.builder()
                .user(user)
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(ClaimStatus.PENDING)
                .build();

        Claim saved = claimRepository.save(claim);
        log.info("Réclamation créée par {} : {}", email, saved.getSubject());

        // 🔔 NOTIFY ADMIN
        String adminMsg = "Nouvelle réclamation de " + user.getPrenom() + " " + user.getNom()
                + " — Sujet : " + saved.getSubject();
        messagingTemplate.convertAndSend("/topic/admin/claims", adminMsg);

        return toDTO(saved);
    }

    // ─── CLIENT: Get own claims ───────────────────────────────────
    public List<ClaimDTO> getMyClaims(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return claimRepository.findByUserId(user.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── ADMIN: Get all claims ────────────────────────────────────
    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── ADMIN: Update claim status ───────────────────────────────
    @Transactional
    public ClaimDTO updateStatus(Long id, ClaimStatus status, String adminResponse) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation introuvable: " + id));
        claim.setStatus(status);
        if (adminResponse != null && !adminResponse.isBlank()) {
            claim.setAdminResponse(adminResponse);
        }
        ClaimDTO dto = toDTO(claimRepository.save(claim));

        // 🔔 NOTIFY CLIENT — WebSocket
        String statusFr = status == ClaimStatus.RESOLVED ? "résolue" : status == ClaimStatus.REJECTED ? "rejetée" : "mise à jour";
        String wsMsg = "Votre réclamation \"" + claim.getSubject() + "\" a été " + statusFr + "."
                + (adminResponse != null && !adminResponse.isBlank() ? " Réponse : " + adminResponse : "");
        messagingTemplate.convertAndSend("/topic/notifications." + claim.getUser().getId(), wsMsg);

        // 🔔 SEND EMAIL
        try {
            emailService.sendClaimStatusEmail(
                    claim.getUser().getEmail(),
                    claim.getUser().getPrenom(),
                    claim.getSubject(),
                    status.name(),
                    adminResponse != null ? adminResponse : "Aucune réponse fournie"
            );
        } catch (Exception e) {
            log.error("Erreur envoi email réclamation à {}: {}", claim.getUser().getEmail(), e.getMessage());
        }

        return dto;
    }

    // ─── Mapper ───────────────────────────────────────────────────
    private ClaimDTO toDTO(Claim claim) {
        return ClaimDTO.builder()
                .id(claim.getId())
                .userId(claim.getUser().getId())
                .userName(claim.getUser().getPrenom() + " " + claim.getUser().getNom())
                .subject(claim.getSubject())
                .description(claim.getDescription())
                .status(claim.getStatus())
                .adminResponse(claim.getAdminResponse())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
