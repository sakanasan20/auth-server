package com.niqdev.authserver.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niqdev.authserver.config.SecurityConfig;
import com.niqdev.authserver.converter.UserConverter;
import com.niqdev.authserver.dto.user.CreateUserRequest;
import com.niqdev.authserver.dto.user.ReplaceUserRequest;
import com.niqdev.authserver.dto.user.UpdateUserRequest;
import com.niqdev.authserver.dto.user.UserDto;
import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.service.admin.UserServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@AutoConfigureMockMvc
@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private UserConverter userConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/admin/users";

    @Test
    void shouldGetUserSuccessfully() throws Exception {
        User user = new User();
        user.setId(1L);
        UserDto dto = new UserDto();
        dto.setId(1L);

        when(userService.getUser(1L)).thenReturn(user);
        when(userConverter.toDto(user)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturnNotFoundIfUserNotExist() throws Exception {
        when(userService.getUser(99L)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL + "/99").with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWithoutAdminRole() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1").with(jwt()))  // 無 ROLE_ADMIN
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "password123", null);
        User user = new User();
        UserDto dto = new UserDto();

        when(userService.createUser(any())).thenReturn(user);
        when(userConverter.toDto(user)).thenReturn(dto);

        mockMvc.perform(post(BASE_URL)
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "not-an-email", "123", null);

        mockMvc.perform(post(BASE_URL)
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        User user = new User();
        UserDto dto = new UserDto();

        when(userService.updateUser(eq(1L), any())).thenReturn(user);
        when(userConverter.toDto(user)).thenReturn(dto);

        mockMvc.perform(patch(BASE_URL + "/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReplaceUser() throws Exception {
        ReplaceUserRequest request = new ReplaceUserRequest();
        request.setUsername("admin");
        request.setEmail("admin@example.com");
        request.setPassword("secret");
        request.setEnabled(true);
        request.setAccountNonLocked(true);
        request.setAccountNonExpired(true);
        request.setCredentialsNonExpired(true);
        request.setRoleIds(Set.of(1L, 2L));
        
        User user = new User();
        UserDto dto = new UserDto();

        when(userService.replaceUser(eq(1L), any())).thenReturn(user);
        when(userConverter.toDto(user)).thenReturn(dto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void shouldSearchUsersWithPagination() throws Exception {
        UserSearchCriteria criteria = new UserSearchCriteria(); // 若有欄位記得補上
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        List<User> users = List.of(new User());
        List<UserDto> dtos = List.of(new UserDto());
        Page<User> page = new PageImpl<>(users, pageable, 1);

        when(userService.searchUsers(any(), any())).thenReturn(page);
        when(userConverter.toDto(any())).thenReturn(dtos.get(0));

        mockMvc.perform(get(BASE_URL + "/search")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }
}
