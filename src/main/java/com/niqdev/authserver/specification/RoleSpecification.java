package com.niqdev.authserver.specification;

import org.springframework.data.jpa.domain.Specification;

import com.niqdev.authserver.dto.role.RoleSearchCriteria;
import com.niqdev.authserver.entity.Role;

import jakarta.persistence.criteria.Predicate;

public class RoleSpecification {

    public static Specification<Role> withCriteria(RoleSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            // 先建立所有的條件
            Predicate predicate = criteriaBuilder.conjunction();

            if (criteria.getName() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + criteria.getName() + "%"));
            }

            if (criteria.getDescription() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("description"), "%" + criteria.getDescription() + "%"));
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
