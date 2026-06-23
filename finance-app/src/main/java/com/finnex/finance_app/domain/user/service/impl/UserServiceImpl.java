package com.finnex.finance_app.domain.user.service.impl;

import com.finnex.finance_app.domain.user.dto.request.UpdateProfile;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import com.finnex.finance_app.domain.user.entity.User;
import com.finnex.finance_app.domain.user.mapper.UserMapper;
import com.finnex.finance_app.domain.user.repository.UserRepository;
import com.finnex.finance_app.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse updateUser(User user, UpdateProfile request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        User updatedUser =
                userRepository.save(user);

        return userMapper.toresponse(updatedUser);
    }
}
