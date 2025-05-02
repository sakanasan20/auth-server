package com.niqdev.authserver.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niqdev.authserver.config.SecurityConfig;
import com.niqdev.authserver.converter.RoleConverter;
import com.niqdev.authserver.dto.role.CreateRoleRequest;
import com.niqdev.authserver.dto.role.ReplaceRoleRequest;
import com.niqdev.authserver.dto.role.RoleDto;
import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.dto.role.UpdateRoleRequest;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.service.admin.RoleService;

@AutoConfigureMockMvc
@WebMvcTest(AdminRoleController.class)
@Import(SecurityConfig.class)
class AdminRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleConverter roleConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("搜尋角色")
    @WithMockUser(roles = "ADMIN")
    void searchRoles_shouldReturnPage() throws Exception {
        RoleSearchCriteria criteria = new RoleSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Role role = new Role();
        RoleDto roleDto = RoleDto.builder().id(1L).name("ADMIN").build();

        Mockito.when(roleService.searchRoles(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(role)));
        Mockito.when(roleConverter.toDto(role)).thenReturn(roleDto);

        mockMvc.perform(get("/api/admin/roles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("取得角色")
    @WithMockUser(roles = "ADMIN")
    void getRole_shouldReturnRoleDto() throws Exception {
        Role role = new Role();
        role.setId(1L);
        Mockito.when(roleService.getRole(1L)).thenReturn(role);
        Mockito.when(roleConverter.toDto(role)).thenReturn(RoleDto.builder().id(1L).name("ADMIN").build());

        mockMvc.perform(get("/api/admin/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("創建角色")
    @WithMockUser(roles = "ADMIN")
    void createRole_shouldReturnCreated() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("USER");

        Role savedRole = new Role();
        savedRole.setId(10L);

        Mockito.when(roleService.createRole(any())).thenReturn(savedRole);
        Mockito.when(roleConverter.toDto(savedRole)).thenReturn(RoleDto.builder().id(10L).name("USER").build());

        mockMvc.perform(post("/api/admin/roles")
        				.with(csrf()) // Spring Security 只對「改變伺服器狀態」的方法開啟 CSRF: POST, PUT, PATCH, DELETE：要 CSRF token; GET, HEAD, OPTIONS, TRACE：預設不驗證 CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("更新角色")
    @WithMockUser(roles = "ADMIN")
    void updateRole_shouldReturnUpdated() throws Exception {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("UPDATED");

        Role updatedRole = new Role();
        updatedRole.setId(2L);

        Mockito.when(roleService.updateRole(eq(2L), any())).thenReturn(updatedRole);
        Mockito.when(roleConverter.toDto(updatedRole)).thenReturn(RoleDto.builder().id(2L).name("UPDATED").build());

        mockMvc.perform(patch("/api/admin/roles/2")
        				.with(csrf()) // Spring Security 只對「改變伺服器狀態」的方法開啟 CSRF: POST, PUT, PATCH, DELETE：要 CSRF token; GET, HEAD, OPTIONS, TRACE：預設不驗證 CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));
    }

    @Test
    @DisplayName("取代角色")
    @WithMockUser(roles = "ADMIN")
    void replaceRole_shouldReturnReplaced() throws Exception {
        ReplaceRoleRequest request = new ReplaceRoleRequest();
        request.setName("REPLACED");

        Role replacedRole = new Role();
        replacedRole.setId(3L);

        Mockito.when(roleService.replaceRole(eq(3L), any())).thenReturn(replacedRole);
        Mockito.when(roleConverter.toDto(replacedRole)).thenReturn(RoleDto.builder().id(3L).name("REPLACED").build());

        mockMvc.perform(put("/api/admin/roles/3")
    					.with(csrf()) // Spring Security 只對「改變伺服器狀態」的方法開啟 CSRF: POST, PUT, PATCH, DELETE：要 CSRF token; GET, HEAD, OPTIONS, TRACE：預設不驗證 CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("REPLACED"));
    }

    @Test
    @DisplayName("刪除角色")
    @WithMockUser(roles = "ADMIN")
    void deleteRole_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/roles/4").with(csrf())) // Spring Security 只對「改變伺服器狀態」的方法開啟 CSRF: POST, PUT, PATCH, DELETE：要 CSRF token; GET, HEAD, OPTIONS, TRACE：預設不驗證 CSRF
                .andExpect(status().isNoContent());
    }
}
