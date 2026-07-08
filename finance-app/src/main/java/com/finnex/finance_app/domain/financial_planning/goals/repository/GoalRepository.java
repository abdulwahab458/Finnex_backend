package com.finnex.finance_app.domain.financial_planning.goals.repository;

import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.entity.Goals;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goals, UUID> {
    List<Goals> findByUserOrderByCreatedAtDesc(User user);
    Optional<Goals> findByIdAndUser(UUID id, User user);
    boolean existsByUserAndName(User user, String name);

}
