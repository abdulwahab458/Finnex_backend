package com.finnex.finance_app.domain.transactions.mapper;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void toResponseMapsTypeAndCategory() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setType(TransactionType.DEBIT);
        transaction.setCategory(TransactionCategory.FOOD_AND_DINING);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.of(2026, 6, 27, 15, 30));
        transaction.setMerchantName("BHola alu");
        transaction.setNotes("Weekend dinner");

        TransactionResponse response = transactionMapper.toResponse(transaction);

        assertThat(response.getAccountId()).isEqualTo(accountId);
        assertThat(response.getType()).isEqualTo(TransactionType.DEBIT);
        assertThat(response.getCategory()).isEqualTo(TransactionCategory.FOOD_AND_DINING);
    }
}
