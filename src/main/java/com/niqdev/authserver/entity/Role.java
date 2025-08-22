package com.niqdev.authserver.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_authorities",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private List<Authority> authorities;
    
    // Constructors
    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(String name, List<Authority> authorities) {
        this.name = name;
        this.authorities = authorities;
    }
    
    // 取得此角色的所有權限
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        
        // 添加角色本身作為權限
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + name));
        
        return grantedAuthorities;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Authority> getAuthorityList() {
        return authorities;
    }
    
    public void setAuthorityList(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
