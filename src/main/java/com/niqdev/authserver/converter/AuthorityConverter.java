package com.niqdev.authserver.converter;

import org.springframework.stereotype.Component;

import com.niqdev.authserver.dto.ReplaceAuthorityRequest;
import com.niqdev.authserver.dto.authority.AuthorityDto;
import com.niqdev.authserver.dto.authority.CreateAuthorityRequest;
import com.niqdev.authserver.dto.authority.UpdateAuthorityRequest;
import com.niqdev.authserver.entity.Authority;

@Component
public class AuthorityConverter {

    public AuthorityDto toDto(Authority authority) {
        if (authority == null) return null;
        return AuthorityDto.builder()
                .id(authority.getId())
                .name(authority.getName())
                .description(authority.getDescription())
                .createdAt(authority.getCreatedAt())
                .createdBy(authority.getCreatedBy())
                .updatedAt(authority.getUpdatedAt())
                .updatedBy(authority.getUpdatedBy())
                .build();
    }

    public Authority toEntity(AuthorityDto dto) {
        if (dto == null) return null;
        return Authority.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedAt(dto.getUpdatedAt())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    public Authority fromCreateRequest(CreateAuthorityRequest request) {
        return Authority.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public Authority fromUpdateRequest(Authority authority, UpdateAuthorityRequest request) {
        if (request.getName() != null) {
            authority.setName(request.getName());
        }
        if (request.getDescription() != null) {
            authority.setDescription(request.getDescription());
        }
        return authority;
    }

    public Authority fromReplaceRequest(ReplaceAuthorityRequest request, Authority existingAuthority) {
        existingAuthority.setName(request.getName());
        existingAuthority.setDescription(request.getDescription());
        return existingAuthority;
    }
}
