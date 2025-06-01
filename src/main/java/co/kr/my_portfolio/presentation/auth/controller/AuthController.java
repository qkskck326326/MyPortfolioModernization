package co.kr.my_portfolio.presentation.auth.controller;

import co.kr.my_portfolio.presentation.auth.dto.login.EmailLoginRequest;
import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import co.kr.my_portfolio.application.auth.LoginService;
import co.kr.my_portfolio.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    // 이메일 로그인
    @PostMapping("/login/email")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithEmail(
            @Valid @RequestBody EmailLoginRequest request) {

        TokenResponse tokens = loginService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String refreshToken = extractTokenFromHeader(request);
        loginService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 되었습니다."));
    }

    // 토큰 재생성
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
