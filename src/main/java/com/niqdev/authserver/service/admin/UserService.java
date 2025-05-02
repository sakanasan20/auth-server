package com.niqdev.authserver.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.niqdev.authserver.dto.user.CreateUserRequest;
import com.niqdev.authserver.dto.user.ReplaceUserRequest;
import com.niqdev.authserver.dto.user.UpdateUserRequest;
import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;

public interface UserService {
	
    Page<User> searchUsers(UserSearchCriteria criteria, Pageable pageable);

    User getUser(Long userId);

    User createUser(CreateUserRequest request);

    User updateUser(Long userId, UpdateUserRequest request);

    User replaceUser(Long userId, ReplaceUserRequest request);

    void deleteUser(Long userId);
}
