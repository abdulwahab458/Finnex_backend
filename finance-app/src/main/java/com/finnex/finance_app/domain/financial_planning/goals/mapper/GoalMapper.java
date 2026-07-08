package com.finnex.finance_app.domain.financial_planning.goals.mapper;

import com.finnex.finance_app.domain.financial_planning.goals.dto.request.CreateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.UpdateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;

import com.finnex.finance_app.domain.financial_planning.goals.entity.Goals;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currentAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Goals toEntity(CreateGoalRequest request);

    GoalResponse toResponse(Goals goal);


}