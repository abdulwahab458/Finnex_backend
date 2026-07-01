package com.finnex.finance_app.domain.portfolio.mapper;

import com.finnex.finance_app.domain.portfolio.dto.reponse.HoldingResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.entity.StockHolding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HoldingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "currentValue", ignore = true)
    @Mapping(target = "dayChangePercent", ignore = true)
    @Mapping(target = "totalReturnPercent", ignore = true)
    StockHolding toEntity(CreateHoldingRequest request);


    @Mapping(target = "symbol", source = "stock.symbol")
    @Mapping(target = "companyName", source = "stock.companyName")
    @Mapping(target = "sector", source = "stock.sector")
    @Mapping(target = "currentPrice", source = "stock.currentPrice")
    @Mapping(target = "previousClose", source = "stock.previousClose")
    HoldingResponse toResponse(StockHolding holding);
}
