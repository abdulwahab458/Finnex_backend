package com.finnex.finance_app.domain.account.Repository;

import com.finnex.finance_app.common.enums.AccountType;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByUser(User user);

    List<Account> findByUserAndAccountType(
            User user,
            AccountType accountType
    );

    Optional<Account> findByUserAndId(
            User user,
            UUID id
    );
}
