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

import com.niqdev.authserver.converter.AuthorityConverter;
import com.niqdev.authserver.dto.ReplaceAuthorityRequest;
import com.niqdev.authserver.dto.authority.AuthorityDto;
import com.niqdev.authserver.dto.authority.AuthoritySearchCriteria;
import com.niqdev.authserver.dto.authority.CreateAuthorityRequest;
import com.niqdev.authserver.dto.authority.UpdateAuthorityRequest;
import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.service.admin.AuthorityServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/authorities")
public class AdminAuthorityController {

    private final AuthorityServiceImpl authorityService;
    private final AuthorityConverter authorityConverter;

    // 搜尋權限
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public Page<AuthorityDto> searchAuthorities(
            @RequestBody AuthoritySearchCriteria criteria,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    	
    	if (criteria == null) {
	        criteria = new AuthoritySearchCriteria(); // 初始化預設條件
	    }
        return authorityService.searchAuthorities(criteria, pageable).map(authorityConverter::toDto);
    }

    // 取得單一權限資料
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{authorityId}")
    public ResponseEntity<AuthorityDto> getAuthority(@PathVariable Long authorityId) {
        Authority authority = authorityService.getAuthority(authorityId);
        return ResponseEntity.ok(authorityConverter.toDto(authority));
    }

    // 建立新權限
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AuthorityDto> createAuthority(@RequestBody @Valid CreateAuthorityRequest request) {
        Authority authority = authorityService.createAuthority(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorityConverter.toDto(authority));
    }

    // 更新權限（部分欄位）
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{authorityId}")
    public ResponseEntity<AuthorityDto> updateAuthority(
            @PathVariable Long authorityId,
            @RequestBody @Valid UpdateAuthorityRequest request) {
        Authority authority = authorityService.updateAuthority(authorityId, request);
        return ResponseEntity.ok(authorityConverter.toDto(authority));
    }

    // 取代權限（全部欄位）
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{authorityId}")
    public ResponseEntity<AuthorityDto> replaceAuthority(
            @PathVariable Long authorityId,
            @RequestBody @Valid ReplaceAuthorityRequest request) {
        Authority authority = authorityService.replaceAuthority(authorityId, request);
        return ResponseEntity.ok(authorityConverter.toDto(authority));
    }

    // 刪除權限
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{authorityId}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable Long authorityId) {
        authorityService.deleteAuthority(authorityId);
        return ResponseEntity.noContent().build();
    }
}
