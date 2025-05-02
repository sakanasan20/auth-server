package com.niqdev.authserver.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.niqdev.authserver.dto.ReplaceAuthorityRequest;
import com.niqdev.authserver.dto.authority.AuthoritySearchCriteria;
import com.niqdev.authserver.dto.authority.CreateAuthorityRequest;
import com.niqdev.authserver.dto.authority.UpdateAuthorityRequest;
import com.niqdev.authserver.entity.Authority;

public interface AuthorityService {

    Page<Authority> searchAuthorities(AuthoritySearchCriteria criteria, Pageable pageable);

    Authority getAuthority(Long authorityId);

    Authority createAuthority(CreateAuthorityRequest request);

    Authority updateAuthority(Long authorityId, UpdateAuthorityRequest request);

    Authority replaceAuthority(Long authorityId, ReplaceAuthorityRequest request);

    void deleteAuthority(Long authorityId);
}
