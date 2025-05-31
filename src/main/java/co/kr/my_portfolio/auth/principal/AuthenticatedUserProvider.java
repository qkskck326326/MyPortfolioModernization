package co.kr.my_portfolio.auth.principal;

import co.kr.my_portfolio.global.jwt.JwtAuthenticationToken;
import co.kr.my_portfolio.user.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public AuthenticatedUser getAuthenticatedUser() {
        // 인증 객체 꺼내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // 인증 객체 저장
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        
        // Long 형태로 변환
        Long userId = Long.parseLong((String) jwt.getPrincipal());
        // Role 형태로 변환
        Role role = Role.valueOf(jwt.getRole());
        
        // 인증 유저로 반환
        return new AuthenticatedUser(userId, role);
    }
}
