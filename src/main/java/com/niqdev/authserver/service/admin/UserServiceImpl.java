package com.niqdev.authserver.service.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.niqdev.authserver.converter.UserConverter;
import com.niqdev.authserver.dto.user.CreateUserRequest;
import com.niqdev.authserver.dto.user.ReplaceUserRequest;
import com.niqdev.authserver.dto.user.UpdateUserRequest;
import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.admin.UserRepository;
import com.niqdev.authserver.specification.UserSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

	private final UserRepository userRepository;
    private final UserConverter userConverter;

    public Page<User> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> specification = UserSpecification.withCriteria(criteria);
        return userRepository.findAll(specification, pageable);
    }

	public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	public User createUser(CreateUserRequest request) {
        User user = userConverter.fromCreateRequest(request);
        return userRepository.save(user);
	}

	public User updateUser(Long userId, UpdateUserRequest request) {
        User existingUser = getUser(userId);
        User updatedUser = userConverter.fromUpdateRequest(existingUser, request);
        return userRepository.save(updatedUser);
	}
	
	public User replaceUser(Long userId, @Valid ReplaceUserRequest request) {
        User existingUser = getUser(userId);
        User replacedUser = userConverter.fromReplaceRequest(request, existingUser);
        return userRepository.save(replacedUser);
	}

	public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
	}

	public void deleteUsers(List<Long> userIds) {
		userRepository.deleteAllById(userIds);
	}
	
}
