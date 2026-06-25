package com.finnex.finance_app.domain.transactions.mapper;

import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "account.id", target = "accountId")
    TransactionResponse toResponse(Transaction transaction);
}