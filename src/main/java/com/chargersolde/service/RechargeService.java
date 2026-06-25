package com.chargersolde.service;

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
    public List<RechargeRequest> getMyRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        return repo.findByClientId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<RechargeRequest> getAllRequests(RechargeStatus status) {
        if (status != null) {
            return repo.findByStatus(status);
        }
        return repo.findAll();
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
}