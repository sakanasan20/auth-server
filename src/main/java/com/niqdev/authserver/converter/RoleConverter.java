package com.niqdev.authserver.converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.niqdev.authserver.dto.role.CreateRoleRequest;
import com.niqdev.authserver.dto.role.ReplaceRoleRequest;
import com.niqdev.authserver.dto.role.RoleDto;
import com.niqdev.authserver.dto.role.UpdateRoleRequest;
import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.repository.admin.AuthorityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleConverter {

	private final AuthorityRepository authorityRepository;
    private final AuthorityConverter authorityConverter;

    public RoleDto toDto(Role role) {
        if (role == null) return null;
        return new RoleDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getAuthorities().stream()
                .map(authorityConverter::toDto)
                .collect(Collectors.toSet()),
            role.getCreatedAt(),
            role.getCreatedBy(),
            role.getUpdatedAt(),
            role.getUpdatedBy()
        );
    }

    public Role toEntity(RoleDto dto) {
        if (dto == null) return null;
        
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .authorities(dto.getAuthorities() != null
                        ? dto.getAuthorities().stream()
                            .map(authorityConverter::toEntity)
                            .collect(Collectors.toSet())
                        : new HashSet<>())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedAt(dto.getUpdatedAt())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    public Role fromCreateRequest(CreateRoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .authorities(findAuthoritiesByIds(request.getAuthorityIds()))
                .build();
    }

    public Role fromUpdateRequest(Role role, UpdateRoleRequest request) {
        if (request.getName() != null) role.setName(request.getName());
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        if (request.getAuthorityIds() != null) role.setAuthorities(findAuthoritiesByIds(request.getAuthorityIds()));
        return role;
    }

    public Role fromReplaceRequest(ReplaceRoleRequest request, Role existingRole) {
        existingRole.setName(request.getName());
        existingRole.setDescription(request.getDescription());
        existingRole.setAuthorities(findAuthoritiesByIds(request.getAuthorityIds()));
        return existingRole;
    }

    private Set<Authority> findAuthoritiesByIds(Set<Long> authorityIds) {
        if (authorityIds == null || authorityIds.isEmpty()) return Collections.emptySet();
        // 假設 AuthorityRepository 用來查詢權限
        return new HashSet<>(authorityRepository.findAllById(authorityIds));
    }
}
