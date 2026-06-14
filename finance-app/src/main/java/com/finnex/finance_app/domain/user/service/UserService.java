package com.finnex.finance_app.domain.user.service;

import com.finnex.finance_app.domain.user.dto.request.UpdateProfile;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import com.finnex.finance_app.domain.user.entity.User;

public interface UserService {
    UserResponse updateUser(User user, UpdateProfile request);
}
