package com.niqdev.authserver.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private String createdBy;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;
    private String updatedBy;
}
