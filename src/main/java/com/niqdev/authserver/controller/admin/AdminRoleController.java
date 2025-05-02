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

import com.niqdev.authserver.converter.RoleConverter;
import com.niqdev.authserver.dto.role.CreateRoleRequest;
import com.niqdev.authserver.dto.role.ReplaceRoleRequest;
import com.niqdev.authserver.dto.role.RoleDto;
import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.dto.role.UpdateRoleRequest;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.service.admin.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

    private final RoleService roleService;
    private final RoleConverter roleConverter;

    // 搜尋角色
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public Page<RoleDto> searchRoles(
            @RequestBody RoleSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    	
    	if (criteria == null) {
	        criteria = new RoleSearchCriteria(); // 初始化預設條件
	    }
        return roleService.searchRoles(criteria, pageable).map(roleConverter::toDto);
    }

    // 取得單一角色資料
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDto> getRole(@PathVariable Long roleId) {
        Role role = roleService.getRole(roleId);
        return ResponseEntity.ok(roleConverter.toDto(role));
    }

    // 創建新角色
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid CreateRoleRequest request) {
        Role role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleConverter.toDto(role));
    }

    // 更新角色
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{roleId}")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long roleId,
            @RequestBody @Valid UpdateRoleRequest request) {
        Role role = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(roleConverter.toDto(role));
    }

    // 取代角色
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDto> replaceRole(
            @PathVariable Long roleId,
            @RequestBody @Valid ReplaceRoleRequest request) {
        Role role = roleService.replaceRole(roleId, request);
        return ResponseEntity.ok(roleConverter.toDto(role));
    }

    // 刪除角色
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
