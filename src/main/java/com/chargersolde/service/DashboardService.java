package com.chargersolde.service;

import com.chargersolde.dto.AdminDashboardDTO;
import com.chargersolde.dto.ClientRechargeSummary;
import com.chargersolde.entity.RechargeStatus;
import com.chargersolde.entity.Role;
import com.chargersolde.entity.User;
import com.chargersolde.repository.RechargeRequestRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final RechargeRequestRepository rechargeRepository;

    public AdminDashboardDTO getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startWeek = now.minusDays(7);
        LocalDateTime startMonth = now.minusMonths(1);

        return AdminDashboardDTO.builder()
                .totalClients(userRepository.countByRole(Role.ROLE_CLIENT))
                .totalRecharges(rechargeRepository.count())
                .todayRecharges(rechargeRepository.countByCreatedAtBetween(startDay, now))
                .weekRecharges(rechargeRepository.countByCreatedAtBetween(startWeek, now))
                .monthRecharges(rechargeRepository.countByCreatedAtBetween(startMonth, now))
                .build();
    }

    public List<ClientRechargeSummary> getClientRechargeSummary() {
        List<User> clients = userRepository.findByRole(Role.ROLE_CLIENT);
        List<ClientRechargeSummary> summaries = new ArrayList<>();

        for (User client : clients) {
            long total = rechargeRepository.countByClientId(client.getId());
            long pending = rechargeRepository.countByClientIdAndStatus(client.getId(), RechargeStatus.PENDING);
            long validated = rechargeRepository.countByClientIdAndStatus(client.getId(), RechargeStatus.VALIDATED);
            long rejected = rechargeRepository.countByClientIdAndStatus(client.getId(), RechargeStatus.REJECTED);
            double totalAmount = rechargeRepository.sumAmountByClientId(client.getId());

            summaries.add(ClientRechargeSummary.builder()
                    .clientId(client.getId())
                    .nom(client.getNom())
                    .prenom(client.getPrenom())
                    .email(client.getEmail())
                    .totalRecharges(total)
                    .totalAmount(totalAmount)
                    .pendingCount(pending)
                    .validatedCount(validated)
                    .rejectedCount(rejected)
                    .build());
        }

        return summaries;
    }
}
