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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niqdev.authserver.config.SecurityConfig;
import com.niqdev.authserver.converter.AuthorityConverter;
import com.niqdev.authserver.dto.ReplaceAuthorityRequest;
import com.niqdev.authserver.dto.authority.AuthorityDto;
import com.niqdev.authserver.dto.authority.AuthoritySearchCriteria;
import com.niqdev.authserver.dto.authority.CreateAuthorityRequest;
import com.niqdev.authserver.dto.authority.UpdateAuthorityRequest;
import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.service.admin.AuthorityServiceImpl;

@AutoConfigureMockMvc
@WebMvcTest(AdminAuthorityController.class)
@Import(SecurityConfig.class)
class AdminAuthorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorityServiceImpl authorityService;

    @MockBean
    private AuthorityConverter authorityConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("搜尋權限")
    @WithMockUser(roles = "ADMIN")
    void searchAuthorities_shouldReturnPage() throws Exception {
        AuthoritySearchCriteria criteria = new AuthoritySearchCriteria();
        Authority authority = new Authority();
        AuthorityDto dto = AuthorityDto.builder().id(1L).name("READ_PRIVILEGE").build();

        Mockito.when(authorityService.searchAuthorities(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(authority)));
        Mockito.when(authorityConverter.toDto(authority)).thenReturn(dto);

        mockMvc.perform(get("/api/admin/authorities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("取得權限")
    @WithMockUser(roles = "ADMIN")
    void getAuthority_shouldReturnDto() throws Exception {
        Authority authority = new Authority();
        authority.setId(1L);

        Mockito.when(authorityService.getAuthority(1L)).thenReturn(authority);
        Mockito.when(authorityConverter.toDto(authority)).thenReturn(AuthorityDto.builder().id(1L).name("READ_PRIVILEGE").build());

        mockMvc.perform(get("/api/admin/authorities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("建立權限")
    @WithMockUser(roles = "ADMIN")
    void createAuthority_shouldReturnCreated() throws Exception {
        CreateAuthorityRequest request = new CreateAuthorityRequest();
        request.setName("WRITE_PRIVILEGE");

        Authority created = new Authority();
        created.setId(2L);

        Mockito.when(authorityService.createAuthority(any())).thenReturn(created);
        Mockito.when(authorityConverter.toDto(created)).thenReturn(AuthorityDto.builder().id(2L).name("WRITE_PRIVILEGE").build());

        mockMvc.perform(post("/api/admin/authorities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("更新權限")
    @WithMockUser(roles = "ADMIN")
    void updateAuthority_shouldReturnUpdated() throws Exception {
        UpdateAuthorityRequest request = new UpdateAuthorityRequest();
        request.setName("UPDATED");

        Authority updated = new Authority();
        updated.setId(3L);

        Mockito.when(authorityService.updateAuthority(eq(3L), any())).thenReturn(updated);
        Mockito.when(authorityConverter.toDto(updated)).thenReturn(AuthorityDto.builder().id(3L).name("UPDATED").build());

        mockMvc.perform(patch("/api/admin/authorities/3")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));
    }

    @Test
    @DisplayName("取代權限")
    @WithMockUser(roles = "ADMIN")
    void replaceAuthority_shouldReturnReplaced() throws Exception {
        ReplaceAuthorityRequest request = new ReplaceAuthorityRequest();
        request.setName("REPLACED");

        Authority replaced = new Authority();
        replaced.setId(4L);

        Mockito.when(authorityService.replaceAuthority(eq(4L), any())).thenReturn(replaced);
        Mockito.when(authorityConverter.toDto(replaced)).thenReturn(AuthorityDto.builder().id(4L).name("REPLACED").build());

        mockMvc.perform(put("/api/admin/authorities/4")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("REPLACED"));
    }

    @Test
    @DisplayName("刪除權限")
    @WithMockUser(roles = "ADMIN")
    void deleteAuthority_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/authorities/5")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
