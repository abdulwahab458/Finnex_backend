package com.finnex.finance_app.domain.user.repository;

import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User,Integer> {
}
