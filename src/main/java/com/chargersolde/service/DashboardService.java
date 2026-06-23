package com.chargersolde.service;

import com.chargersolde.dto.AdminDashboardDTO;
import com.chargersolde.entity.Role;
import com.chargersolde.repository.RechargeRequestRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {


    private final UserRepository userRepository;

    private final RechargeRequestRepository rechargeRepository;



    public AdminDashboardDTO getDashboard(){



        LocalDateTime now = LocalDateTime.now();



        LocalDateTime startDay =
                now.toLocalDate()
                        .atStartOfDay();



        LocalDateTime startWeek =
                now.minusDays(7);



        LocalDateTime startMonth =
                now.minusMonths(1);



        return AdminDashboardDTO.builder()


                .totalClients(
                        userRepository.countByRole(Role.ROLE_CLIENT)
                )


                .totalRecharges(
                        rechargeRepository.count()
                )


                .todayRecharges(
                        rechargeRepository.countByCreatedAtBetween(
                                startDay,
                                now
                        )
                )


                .weekRecharges(
                        rechargeRepository.countByCreatedAtBetween(
                                startWeek,
                                now
                        )
                )


                .monthRecharges(
                        rechargeRepository.countByCreatedAtBetween(
                                startMonth,
                                now
                        )
                )


                .build();


    }



}