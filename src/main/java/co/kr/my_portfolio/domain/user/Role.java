package co.kr.my_portfolio.domain.user;

public enum Role {
    USER,
    ADMIN;

    public String toAuthority() {
        return "ROLE_" + this.name(); // ex: "ADMIN" → "ROLE_ADMIN"
    }
}