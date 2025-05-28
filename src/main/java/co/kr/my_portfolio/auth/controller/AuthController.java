package co.kr.my_portfolio.auth.controller;

import co.kr.my_portfolio.auth.dto.login.EmailLoginRequest;
import co.kr.my_portfolio.auth.dto.jwt.ReissueRequest;
import co.kr.my_portfolio.auth.dto.jwt.TokenResponse;
import co.kr.my_portfolio.auth.dto.login.LogoutRequest;
import co.kr.my_portfolio.auth.service.LoginService;
import co.kr.my_portfolio.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody @Valid LogoutRequest request) {
        loginService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 되었습니다."));
    }
    
    // 토큰 재생성
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestBody @Valid ReissueRequest request) {
        TokenResponse tokenResponse = loginService.reissue(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }


}
