package co.kr.my_portfolio.user.domain;

public enum Role {
    USER,
    ADMIN;

    public String toAuthority() {
        return "ROLE_" + this.name(); // ex: "ADMIN" → "ROLE_ADMIN"
    }
}