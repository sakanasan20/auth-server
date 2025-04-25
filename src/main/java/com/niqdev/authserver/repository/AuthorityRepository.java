package com.niqdev.authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niqdev.authserver.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	Optional<Authority> findByName(String name);
}
