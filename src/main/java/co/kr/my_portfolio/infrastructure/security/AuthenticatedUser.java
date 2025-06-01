package co.kr.my_portfolio.infrastructure.security;

import co.kr.my_portfolio.domain.user.Role;

public class AuthenticatedUser {
    private final Long id;
    private final Role role;

    public AuthenticatedUser(Long id, Role role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }
}
