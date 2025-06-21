package co.kr.my_portfolio.infrastructure.jwt;

import co.kr.my_portfolio.global.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        String path = request.getRequestURI();

        // 헤더에 토큰 없는 요청은 통과
        if (token == null || path.equals("/api/auth/reissue")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 유효하지 않은 토큰으로 보내는 요청 반려
        if (!StringUtils.hasText(token) || !jwtProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 유효한 토큰일시 인증객체 생성
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

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        CommonResponse<Void> commonResponse = CommonResponse.fail(message);
        objectMapper.writeValue(response.getWriter(), commonResponse);
        response.getWriter().flush();
    }
}
