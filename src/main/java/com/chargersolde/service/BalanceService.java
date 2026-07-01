package com.chargersolde.service;

import com.chargersolde.dto.ClientBalanceResponse;
import com.chargersolde.dto.DailyBalanceDTO;
import com.chargersolde.entity.ClientBalanceHistory;
import com.chargersolde.entity.RechargeStatus;
import com.chargersolde.entity.User;
import com.chargersolde.repository.ClientBalanceHistoryRepository;
import com.chargersolde.repository.RechargeRequestRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {


    private final ClientBalanceHistoryRepository repo;
    private final UserRepository userRepo;
    private final RechargeRequestRepository rechargeRepo;



    @Transactional
    public void generateDailyBalance(){


        LocalDate today = LocalDate.now();


        List<User> clients=userRepo.findAll();


        for(User client:clients){


            Double amount =
                    rechargeRepo.sumAmountByClientIdAndStatus(
                            client.getId(),
                            RechargeStatus.VALIDATED
                    );


            long count =
                    rechargeRepo.countByClientIdAndStatus(
                            client.getId(),
                            RechargeStatus.VALIDATED
                    );



            ClientBalanceHistory h =
                    ClientBalanceHistory.builder()
                            .client(client)
                            .date(today)
                            .totalAmount(amount)
                            .validatedOrders((int)count)
                            .paid(false)
                            .build();


            repo.save(h);


        }


    }






    public ClientBalanceResponse getBalance(
            Long clientId,
            int days
    ){


        User user =
                userRepo.findById(clientId)
                        .orElseThrow();



        List<DailyBalanceDTO> result =
                new ArrayList<>();


        double total = 0;
        int totalOrders = 0;



        LocalDate today = LocalDate.now();



        for(int i=0;i<days;i++){


            LocalDate date =
                    today.minusDays(i);



            LocalDateTime start =
                    date.atStartOfDay();


            LocalDateTime end =
                    date.atTime(23,59,59);



            Double amount =
                    rechargeRepo.sumAmountByClientAndDate(
                            clientId,
                            RechargeStatus.VALIDATED,
                            start,
                            end
                    );


            Long orders =
                    rechargeRepo.countByClientAndDate(
                            clientId,
                            RechargeStatus.VALIDATED,
                            start,
                            end
                    );


            if(amount == null){
                amount = 0D;
            }



            result.add(
                    new DailyBalanceDTO(
                            date,
                            amount,
                            orders.intValue()
                    )
            );


            total += amount;

            totalOrders += orders.intValue();

        }



        return new ClientBalanceResponse(
                clientId,
                user.getEmail(),
                days,
                total,
                totalOrders,
                result
        );

    }





    @Transactional
    public void payClient(Long clientId){


        List<ClientBalanceHistory> list =
                repo.findByClientIdAndDateBetween(
                        clientId,
                        LocalDate.MIN,
                        LocalDate.now()
                );


        list.forEach(x->x.setPaid(true));


        repo.saveAll(list);


    }

}