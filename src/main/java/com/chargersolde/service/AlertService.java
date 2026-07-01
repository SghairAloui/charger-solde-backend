package com.chargersolde.service;

import com.chargersolde.dto.CreateAlertDTO;
import com.chargersolde.entity.Role;
import com.chargersolde.entity.SystemAlert;
import com.chargersolde.entity.User;
import com.chargersolde.repository.SystemAlertRepository;
import com.chargersolde.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {


    private final SystemAlertRepository repo;

    private final UserRepository userRepository;

    private final NotificationService notificationService;



    @Transactional
    public SystemAlert sendAlert(CreateAlertDTO dto){


        SystemAlert alert =
                SystemAlert.builder()
                        .title(dto.getTitle())
                        .message(dto.getMessage())
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build();



        SystemAlert saved = repo.save(alert);



        List<User> clients =
                userRepository.findByRole(Role.ROLE_CLIENT);



        for(User client : clients){


            notificationService.notifyClient(
                    client.getId(),
                    "⚠️ "+dto.getTitle()
                            +"\n"
                            +dto.getMessage()
            );


        }



        return saved;

    }




    public List<SystemAlert> getActiveAlerts(){

        return repo.findByActiveTrueOrderByCreatedAtDesc();

    }



    @Transactional
    public void disable(Long id){


        SystemAlert alert =
                repo.findById(id)
                        .orElseThrow();


        alert.setActive(false);


    }

}