package com.niqdev.authserver.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.niqdev.authserver.dto.role.CreateRoleRequest;
import com.niqdev.authserver.dto.role.ReplaceRoleRequest;
import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.dto.role.UpdateRoleRequest;
import com.niqdev.authserver.entity.Role;

public interface RoleService {

    Page<Role> searchRoles(RoleSearchCriteria criteria, Pageable pageable);

    Role getRole(Long roleId);

    Role createRole(CreateRoleRequest request);

    Role updateRole(Long roleId, UpdateRoleRequest request);

    Role replaceRole(Long roleId, ReplaceRoleRequest request);

    void deleteRole(Long roleId);
}
