package com.niqdev.authserver.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.niqdev.authserver.converter.AuthorityConverter;
import com.niqdev.authserver.dto.authority.AuthoritySearchCriteria;
import com.niqdev.authserver.dto.authority.CreateAuthorityRequest;
import com.niqdev.authserver.dto.authority.ReplaceAuthorityRequest;
import com.niqdev.authserver.dto.authority.UpdateAuthorityRequest;
import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.repository.admin.AuthorityRepository;
import com.niqdev.authserver.specification.AuthoritySpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityConverter authorityConverter;

    // 搜尋權限
    @Override
    public Page<Authority> searchAuthorities(AuthoritySearchCriteria criteria, Pageable pageable) {
        Specification<Authority> specification = AuthoritySpecification.withCriteria(criteria);
        return authorityRepository.findAll(specification, pageable);
    }

    // 取得單一權限
    @Override
    public Authority getAuthority(Long authorityId) {
        return authorityRepository.findById(authorityId)
                .orElseThrow(() -> new EntityNotFoundException("Authority not found"));
    }

    // 建立權限
    @Override
    public Authority createAuthority(CreateAuthorityRequest request) {
        Authority authority = authorityConverter.fromCreateRequest(request);
        return authorityRepository.save(authority);
    }

    // 更新權限
    @Override
    public Authority updateAuthority(Long authorityId, UpdateAuthorityRequest request) {
        Authority existing = getAuthority(authorityId);
        Authority updated = authorityConverter.fromUpdateRequest(existing, request);
        return authorityRepository.save(updated);
    }

    // 取代權限
    @Override
    public Authority replaceAuthority(Long authorityId, ReplaceAuthorityRequest request) {
        Authority existing = getAuthority(authorityId);
        Authority replaced = authorityConverter.fromReplaceRequest(request, existing);
        return authorityRepository.save(replaced);
    }

    // 刪除權限
    @Override
    public void deleteAuthority(Long authorityId) {
        authorityRepository.deleteById(authorityId);
    }
}
