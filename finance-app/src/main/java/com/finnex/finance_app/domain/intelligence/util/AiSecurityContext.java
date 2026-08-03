package com.finnex.finance_app.domain.intelligence.util;

import com.finnex.finance_app.common.exceptions.UnauthorizedException;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AiSecurityContext {

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new UnauthorizedException(
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new UnauthorizedException(
                    "Invalid authenticated user"
            );
        }

        return user;
    }
}
