package co.kr.my_portfolio.presentation.auth.controller;

import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import co.kr.my_portfolio.application.auth.LoginService;
import co.kr.my_portfolio.global.response.ApiResponse;
import co.kr.my_portfolio.presentation.auth.dto.login.EmailLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    // 이메일 로그인
    @Operation(summary = "이메일 로그인",
            description = """
                    API 사용 조건
                    - email, password 생략 불가.
                    """)
    @PostMapping("/login/email")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithEmail(
            @Valid @RequestBody EmailLoginRequest request) {

        TokenResponse tokens = loginService.login(request.toCommand());
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    // 로그아웃
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "로그아웃",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    """)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String refreshToken = extractTokenFromHeader(request);
        loginService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 되었습니다."));
    }

    // 토큰 재생성
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Access 토큰 재생성",
            description = """
                    API 사용 조건
                    - JWT 인증( Refresh Token )
                    """)
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(HttpServletRequest request) {
        String refreshToken = extractTokenFromHeader(request);
        TokenResponse tokenResponse = loginService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    // Header에서 Bearer 토큰 꺼내는 공통 메서드
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
    }


}
