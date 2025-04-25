package com.niqdev.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niqdev.authserver.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
