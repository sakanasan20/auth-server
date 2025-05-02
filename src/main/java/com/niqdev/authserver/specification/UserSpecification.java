package com.niqdev.authserver.specification;

import org.springframework.data.jpa.domain.Specification;

import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<User> withCriteria(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            // 先建立所有的條件
            Predicate predicate = criteriaBuilder.conjunction();

            if (criteria.getUsername() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("username"), "%" + criteria.getUsername() + "%"));
            }

            if (criteria.getEmail() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("email"), "%" + criteria.getEmail() + "%"));
            }

            if (criteria.getEnabled() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("enabled"), criteria.getEnabled()));
            }

            if (criteria.getAccountNonLocked() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("accountNonLocked"), criteria.getAccountNonLocked()));
            }

            if (criteria.getAccountNonExpired() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("accountNonExpired"), criteria.getAccountNonExpired()));
            }

            if (criteria.getCredentialsNonExpired() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("credentialsNonExpired"), criteria.getCredentialsNonExpired()));
            }

            if (criteria.getCreatedAtFrom() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtFrom()));
            }

            if (criteria.getCreatedAtTo() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtTo()));
            }

            if (criteria.getCreatedBy() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("createdBy"), "%" + criteria.getCreatedBy() + "%"));
            }

            if (criteria.getUpdatedAtFrom() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), criteria.getUpdatedAtFrom()));
            }

            if (criteria.getUpdatedAtTo() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), criteria.getUpdatedAtTo()));
            }

            if (criteria.getUpdatedBy() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("updatedBy"), "%" + criteria.getUpdatedBy() + "%"));
            }

            return predicate;
        };
    }
}
