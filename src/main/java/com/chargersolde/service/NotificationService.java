package com.chargersolde.service;

import com.chargersolde.entity.RechargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Notification admin
    public void notifyAdmin(RechargeRequest request) {
        System.out.println("📢 ADMIN NOTIF: " + request.getPhoneNumber());

        messagingTemplate.convertAndSend(
                "/topic/admin/recharges",
                "New recharge request from " + request.getPhoneNumber()
        );
    }
    // Notification client
    public void notifyClient(String email, String message) {
        System.out.println("📢 CLIENT NOTIF: " + email + " -> " + message);

        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                message
        );
    }}
