package com.niqdev.authserver.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niqdev.authserver.converter.UserConverter;
import com.niqdev.authserver.dto.PageResponse;
import com.niqdev.authserver.dto.user.CreateUserRequest;
import com.niqdev.authserver.dto.user.ReplaceUserRequest;
import com.niqdev.authserver.dto.user.UpdateUserRequest;
import com.niqdev.authserver.dto.user.UserDto;
import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.service.admin.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {
	
	private final UserServiceImpl userService;
    private final UserConverter userConverter;
	
	// 搜尋使用者
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/search")
	public PageResponse<UserDto> searchUsers(
			@RequestBody UserSearchCriteria criteria,
	        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		
		if (criteria == null) {
	        criteria = new UserSearchCriteria(); // 初始化預設條件
	    }
		Page<UserDto> page = userService.searchUsers(criteria, pageable).map(userConverter::toDto);
		return new PageResponse<>(page);
	}

    // 取得單一使用者資料
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(userConverter.toDto(user));
    }
	
    // 創建新使用者
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(user));
    }
    
    // 更新使用者
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserRequest request) {
        User user = userService.updateUser(userId, request);
        return ResponseEntity.ok(userConverter.toDto(user));
    }

    // 取代使用者
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> replaceUser(
            @PathVariable Long userId,
            @RequestBody @Valid ReplaceUserRequest request) {
        User user = userService.replaceUser(userId, request);
        return ResponseEntity.ok(userConverter.toDto(user));
    }

    // 刪除使用者
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
