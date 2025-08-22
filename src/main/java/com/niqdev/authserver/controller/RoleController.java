package com.niqdev.authserver.controller;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    // Role 管理 API
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @GetMapping("/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Role> createRole(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        @SuppressWarnings("unchecked")
        List<String> authorityNames = (List<String>) request.get("authorities");
        
        Role role = roleService.createRole(name, authorityNames);
        return ResponseEntity.ok(role);
    }
    
    @PutMapping("/{name}/authorities")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Role> updateRoleAuthorities(
            @PathVariable String name,
            @RequestBody List<String> authorityNames) {
        Role role = roleService.updateRoleAuthorities(name, authorityNames);
        return ResponseEntity.ok(role);
    }
    
    @DeleteMapping("/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable String name) {
        roleService.deleteRole(name);
        return ResponseEntity.ok().build();
    }
    
    // Authority 管理 API
    @GetMapping("/authorities")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Authority>> getAllAuthorities() {
        return ResponseEntity.ok(roleService.getAllAuthorities());
    }
    
    @GetMapping("/authorities/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Authority> getAuthorityByName(@PathVariable String name) {
        return roleService.getAuthorityByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/authorities")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Authority> createAuthority(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        Authority authority = roleService.createAuthority(name);
        return ResponseEntity.ok(authority);
    }
    
    @DeleteMapping("/authorities/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteAuthority(@PathVariable String name) {
        roleService.deleteAuthority(name);
        return ResponseEntity.ok().build();
    }
    
    // User 角色管理 API
    @GetMapping("/users/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or #username == authentication.name")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String username) {
        List<Role> roles = roleService.getUserRoles(username);
        return ResponseEntity.ok(roles);
    }
    
    @PostMapping("/users/{username}/roles/{roleName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable String username,
            @PathVariable String roleName) {
        roleService.assignRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/users/{username}/roles/{roleName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable String username,
            @PathVariable String roleName) {
        roleService.removeRoleFromUser(username, roleName);
        return ResponseEntity.ok().build();
    }
}
