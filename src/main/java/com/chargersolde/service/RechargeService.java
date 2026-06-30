package com.chargersolde.service;

import com.chargersolde.dto.ClientBalanceDTO;
import com.chargersolde.dto.CreateRechargeRequestDTO;
import com.chargersolde.entity.RechargePlan;
import com.chargersolde.entity.RechargeRequest;
import com.chargersolde.entity.RechargeStatus;
import com.chargersolde.entity.User;
import com.chargersolde.repository.RechargePlanRepository;
import com.chargersolde.repository.RechargeRequestRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeService {

    private final RechargeRequestRepository repo;
    private final RechargePlanRepository planRepo;
    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public RechargeRequest createRequest(CreateRechargeRequestDTO dto, String userEmail) {

        RechargePlan plan = planRepo.findById(dto.getPlanId())
                .orElseThrow();

        User client = userRepository.findByEmail(userEmail)
                .orElseThrow();

        RechargeRequest request = RechargeRequest.builder()
                .phoneNumber(dto.getPhoneNumber())
                .amount(dto.getAmount())
                .plan(plan)
                .client(client)
                .status(RechargeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        RechargeRequest saved = repo.save(request);

        // 🔔 NOTIFY ADMIN
        notificationService.notifyAdmin(saved);

        // 🔔 NOTIFY CLIENT — confirmation
        String operatorName = plan.getOperator().getName();
        notificationService.notifyClient(
                client.getId(),
                "Votre demande de recharge " + operatorName + " (" + plan.getLabel()
                        + ") a été soumise avec succès. Montant : " + saved.getAmount() + " TND"
        );

        return saved;
    }
    @Transactional(readOnly = true)
    public Page<RechargeRequest> getMyRequests(String email, int page, int size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Pageable pageable = PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by("createdAt").descending()
        );

        return repo.findByClientId(user.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<RechargeRequest> getAllRequests(RechargeStatus status, int page) {

        Pageable pageable = PageRequest.of(
                page,
                10,
                org.springframework.data.domain.Sort.by("createdAt").descending()
        );

        if (status != null) {
            return repo.findByStatus(status, pageable);
        }

        return repo.findAll(pageable);
    }

    public RechargeRequest validate(Long id, boolean accept) {

        RechargeRequest req = repo.findById(id)
                .orElseThrow();

        req.setStatus(accept ? RechargeStatus.VALIDATED : RechargeStatus.REJECTED);

        RechargeRequest saved = repo.save(req);

        // 🔔 NOTIFY CLIENT — validation ou rejet
        String operatorName = req.getPlan().getOperator().getName();
        String statusFr = accept ? "validée" : "rejetée";
        String message = "Votre demande de recharge " + operatorName
                + " (" + req.getPlan().getLabel() + ") a été " + statusFr
                + ". Montant : " + req.getAmount() + " TND";

        notificationService.notifyClient(req.getClient().getId(), message);

        return saved;
    }

    public RechargeRequest adminCancel(Long id, String message) {

        RechargeRequest req = repo.findById(id)
                .orElseThrow();

        // ❌ trop tard si déjà livré
        if (req.getStatus() == RechargeStatus.DELIVERED) {
            throw new IllegalStateException("Recharge déjà livrée, impossible d'annuler");
        }

        // ❌ si déjà en cours de traitement (optionnel si tu ajoutes plus tard)
        if (req.getStatus() == RechargeStatus.REJECTED) {
            throw new IllegalStateException("Recharge déjà rejetée");
        }

        req.setStatus(RechargeStatus.ADMIN_CANCELLED);
        req.setAdminMessage(message);

        RechargeRequest saved = repo.save(req);

        notificationService.notifyClient(
                req.getClient().getId(),
                "❌ Votre recharge a été annulée par l'admin. Raison : " + message
        );

        return saved;
    }

    public RechargeRequest process(Long id) {

        RechargeRequest req = repo.findById(id).orElseThrow();

        // 🔴 check important
        if (req.getStatus() != RechargeStatus.VALIDATED) {
            throw new IllegalStateException("Cannot process non-validated request");
        }

        // ⚠️ double sécurité
        if (req.getStatus() == RechargeStatus.ADMIN_CANCELLED) {
            throw new IllegalStateException("Cancelled by admin");
        }

        req.setStatus(RechargeStatus.DELIVERED);
        return repo.save(req);
    }

    @Transactional(readOnly = true)
    public List<ClientBalanceDTO> getAllClientBalances() {

        List<Object[]> results = repo.getBalancesByClients();

        return results.stream()
                .map(row -> {

                    Long clientId = (Long) row[0];
                    Double balance = ((Number) row[1]).doubleValue();

                    User user = userRepository.findById(clientId)
                            .orElseThrow();

                    return new ClientBalanceDTO(
                            clientId,
                            user.getEmail(),
                            balance
                    );

                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RechargeRequest> getMyRequests(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return repo.findByClientId(user.getId());
    }
}