package com.mport.domain.service;

import com.mport.domain.dto.UserResponse;
import com.mport.domain.model.User;
import com.mport.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponse getUserInfoById(Long id) {
        log.debug("Getting user info by id: {}", id);

        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: %s.".formatted(id)));

        return  modelMapper.map(user, UserResponse.class);
    }

}
