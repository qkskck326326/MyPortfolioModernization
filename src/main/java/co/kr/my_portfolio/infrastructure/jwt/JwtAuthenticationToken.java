package co.kr.my_portfolio.infrastructure.jwt;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String userId;
    private final String role;
    private final Date expiration;

    public JwtAuthenticationToken(String userId, String role, Date expiration, Collection<? extends GrantedAuthority> authorities) {
        super(userId, null, authorities); // principal = userId, credentials = null
        this.userId = userId;
        this.role = role;
        this.expiration = expiration;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
