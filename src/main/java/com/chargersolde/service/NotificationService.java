package com.chargersolde.service;

import com.chargersolde.entity.RechargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Notification admin — when a client creates a recharge
    public void notifyAdmin(RechargeRequest request) {
        String clientName = request.getClient().getPrenom() + " " + request.getClient().getNom();
        String operatorName = request.getPlan().getOperator().getName();

        String message = "Nouvelle demande de recharge de " + clientName
                + " — Opérateur : " + operatorName
                + " — Montant : " + request.getAmount() + " TND";

        log.info("Notification admin: {}", message);

        messagingTemplate.convertAndSend(
                "/topic/admin/recharges",
                message
        );
    }

    // Notification client — via Topic (works without Spring Security session)
    public void notifyClient(Long clientId, String message) {
        log.info("Notification client {}: {}", clientId, message);

        messagingTemplate.convertAndSend(
                "/topic/notifications." + clientId,
                message
        );
    }
}
