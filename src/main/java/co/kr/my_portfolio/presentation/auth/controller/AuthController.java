package co.kr.my_portfolio.presentation.auth.controller;

import co.kr.my_portfolio.global.exception.custom.UnauthorizedException;
import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import co.kr.my_portfolio.application.auth.LoginService;
import co.kr.my_portfolio.global.response.CommonResponse;
import co.kr.my_portfolio.presentation.auth.dto.login.EmailLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    // 이메일 로그인
    @Operation(summary = "이메일 로그인",
            description = """
                    API 사용 조건
                    - email, password 생략 불가.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류 (validation 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)")
    })
    @PostMapping("/login/email")
    public ResponseEntity<CommonResponse<String>> loginWithEmail(
            @Valid @RequestBody EmailLoginRequest request) {

        TokenResponse tokens = loginService.login(request.toCommand());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경이면 true
                .path("/api/auth/reissue")
                .path("/api/auth/logout")
                .sameSite("Lax") // or Lax, None (상황에 따라)
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(CommonResponse.success(tokens.getAccessToken()));
    }

    // 로그아웃
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "로그아웃",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 로그아웃됨"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 또는 만료된 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        loginService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(CommonResponse.success(null, "로그아웃 되었습니다."));

    }

    // 토큰 재생성
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Access 토큰 재생성",
            description = """
                    API 사용 조건
                    - JWT 인증( Refresh Token )
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access 토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh 토큰이 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "만료된 토큰이거나 접근 권한 없음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponse>> reissue(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new UnauthorizedException("RefreshToken 쿠키가 없습니다.");
        }
        TokenResponse tokenResponse = loginService.reissue(refreshToken);
        return ResponseEntity.ok(CommonResponse.success(tokenResponse));
    }

    // Header에서 Bearer 토큰 꺼내는 공통 메서드
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
    }
    
    // 쿠키에서 Refresh Token 꺼내는 메소드
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


}
