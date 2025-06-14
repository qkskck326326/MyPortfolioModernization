package co.kr.my_portfolio.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json; charset=UTF-8");

        String json = """
            {
                "status": 403,
                "error": "Forbidden",
                "message": "권한이 없습니다. 관리자 권한이 필요합니다.",
                "path": "%s"
            }
            """.formatted(request.getRequestURI());

        response.getWriter().write(json);
    }
}
