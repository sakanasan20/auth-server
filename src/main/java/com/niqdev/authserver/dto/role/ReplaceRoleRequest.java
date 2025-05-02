package com.niqdev.authserver.dto.role;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceRoleRequest {

    @NotBlank
    private String name;

    private String description;
    
    private Set<Long> authorityIds;
}
