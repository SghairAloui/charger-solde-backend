package com.chargersolde.repository;

import com.chargersolde.entity.AccountStatus;
import com.chargersolde.entity.Role;
import com.chargersolde.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetPasswordToken(String token);
    long countByRole(Role role);
    java.util.List<User> findByRole(Role role);

    List<User> findByAccountStatus(AccountStatus status);

    List<User> findByRoleAndCreatedByAdmin(
            Role role,
            boolean createdByAdmin
    );

    List<User> findByRoleAndAccountStatusIn(
            Role role,
            List<AccountStatus> statuses
    );
}
