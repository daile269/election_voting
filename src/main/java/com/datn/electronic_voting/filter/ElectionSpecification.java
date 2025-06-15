package com.datn.electronic_voting.filter;

import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.enums.ElectionStatus;
import org.springframework.data.jpa.domain.Specification;

public class ElectionSpecification {
    public static Specification<Election> hasTitleLike(String searchTerm) {
        return (root, query, builder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get("title")), "%" + searchTerm.toLowerCase() + "%");
        };
    }

    public static Specification<Election> hasStatus(ElectionStatus status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }
}
