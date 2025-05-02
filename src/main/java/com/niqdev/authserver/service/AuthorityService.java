package com.niqdev.authserver.service;

import org.springframework.stereotype.Service;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.repository.AuthorityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityService {

	private final AuthorityRepository authorityRepository;

	public Authority create(String name) {
		return authorityRepository.saveAndFlush(Authority.builder().name(name).build());
	}
	
}
