package com.finnex.finance_app.domain.transactions.mapper;

import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "category", target = "category")
    TransactionResponse toResponse(Transaction transaction);
}
