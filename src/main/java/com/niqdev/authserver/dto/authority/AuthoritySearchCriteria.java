package com.niqdev.authserver.dto.authority;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthoritySearchCriteria {
    private String name;
    private String description;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private String createdBy;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;
    private String updatedBy;
}
