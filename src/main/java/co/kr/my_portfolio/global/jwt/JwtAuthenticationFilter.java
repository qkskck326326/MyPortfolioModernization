package co.kr.my_portfolio.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserId(token);
            String role = jwtProvider.getRole(token).toAuthority();
            Date expiration = jwtProvider.getExpiration(token);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(role)
            );
            
            // 인증 객체 생성
            JwtAuthenticationToken authentication =
                    new JwtAuthenticationToken(userId, role, expiration, authorities);

            // 사용자 IP 및 세션 정보 저장(세션은 안쓰임)
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 요청 흐름에서 사용하기 위해 SecurityContextHolder 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
