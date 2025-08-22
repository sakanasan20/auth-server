package com.niqdev.authserver.service;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.AuthorityRepository;
import com.niqdev.authserver.repository.RoleRepository;
import com.niqdev.authserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    
    public RoleService(RoleRepository roleRepository, 
                      AuthorityRepository authorityRepository,
                      UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }
    
    // Role 管理方法
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    public Role createRole(String name, List<String> authorityNames) {
        List<Authority> authorities = authorityNames.stream()
                .map(authName -> authorityRepository.findByName(authName)
                        .orElseGet(() -> authorityRepository.save(new Authority(authName))))
                .toList();
        
        Role role = new Role(name, authorities);
        return roleRepository.save(role);
    }
    
    public Role updateRoleAuthorities(String roleName, List<String> authorityNames) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        List<Authority> authorities = authorityNames.stream()
                .map(authName -> authorityRepository.findByName(authName)
                        .orElseGet(() -> authorityRepository.save(new Authority(authName))))
                .toList();
        
        role.setAuthorityList(authorities);
        return roleRepository.save(role);
    }
    
    public void deleteRole(String name) {
        roleRepository.findByName(name).ifPresent(roleRepository::delete);
    }
    
    // Authority 管理方法
    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll();
    }
    
    public Optional<Authority> getAuthorityByName(String name) {
        return authorityRepository.findByName(name);
    }
    
    public Authority createAuthority(String name) {
        return authorityRepository.save(new Authority(name));
    }
    
    public void deleteAuthority(String name) {
        authorityRepository.findByName(name).ifPresent(authorityRepository::delete);
    }
    
    // User 角色管理方法
    public void assignRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        List<Role> currentRoles = user.getRoles();
        if (!currentRoles.contains(role)) {
            currentRoles.add(role);
            user.setRoles(currentRoles);
            userRepository.save(user);
        }
    }
    
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        List<Role> currentRoles = user.getRoles();
        currentRoles.remove(role);
        user.setRoles(currentRoles);
        userRepository.save(user);
    }
    
    public List<Role> getUserRoles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getRoles();
    }
}
