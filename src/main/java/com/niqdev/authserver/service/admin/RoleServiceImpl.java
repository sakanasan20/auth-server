package com.niqdev.authserver.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.niqdev.authserver.converter.RoleConverter;
import com.niqdev.authserver.dto.role.CreateRoleRequest;
import com.niqdev.authserver.dto.role.ReplaceRoleRequest;
import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.dto.role.UpdateRoleRequest;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.repository.admin.RoleRepository;
import com.niqdev.authserver.specification.RoleSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleConverter roleConverter;
    
    // 搜尋角色
    @Override
    public Page<Role> searchRoles(RoleSearchCriteria criteria, Pageable pageable) {
        Specification<Role> specification = RoleSpecification.withCriteria(criteria);
        return roleRepository.findAll(specification, pageable);
    }

    // 取得單一角色資料
    @Override
    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }

    // 創建新角色
    @Override
    public Role createRole(CreateRoleRequest request) {
        Role role = roleConverter.fromCreateRequest(request);
        return roleRepository.save(role);
    }

    // 更新角色
    @Override
    public Role updateRole(Long roleId, UpdateRoleRequest request) {
        Role existingRole = getRole(roleId);
        Role updatedRole = roleConverter.fromUpdateRequest(existingRole, request);
        return roleRepository.save(updatedRole);
    }

    // 取代角色
    @Override
    public Role replaceRole(Long roleId, ReplaceRoleRequest request) {
        Role existingRole = getRole(roleId);
        Role replacedRole = roleConverter.fromReplaceRequest(request, existingRole);
        return roleRepository.save(replacedRole);
    }

    // 刪除角色
    @Override
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}
