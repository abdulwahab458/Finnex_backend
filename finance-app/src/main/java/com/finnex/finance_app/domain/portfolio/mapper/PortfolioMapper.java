package com.finnex.finance_app.domain.portfolio.mapper;

import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.user.dto.request.RegisterRequestDTO;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import com.finnex.finance_app.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(
            target = "totalInvested",
            expression = "java(java.math.BigDecimal.ZERO)"
    )
    @Mapping(
            target = "currentValue",
            expression = "java(java.math.BigDecimal.ZERO)"
    )
    @Mapping(
            target = "totalReturnPercent",
            expression = "java(java.math.BigDecimal.ZERO)"
    )
    Portfolio toEntity(CreatePortfolioRequest request);
    PortfolioResponse toResponse(Portfolio portfolio);

}
