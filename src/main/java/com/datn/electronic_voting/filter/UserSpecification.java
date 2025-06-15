package com.datn.electronic_voting.filter;

import com.datn.electronic_voting.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasFullName(String fullName) {
        return (root, query, cb) -> fullName == null || fullName.isEmpty()
                ? null
                : cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> username == null || username.isEmpty()
                ? null
                : cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> email == null || email.isEmpty()
                ? null
                : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> role == null || role.isEmpty()
                ? null
                : cb.equal(root.get("role"), role);
    }
}
