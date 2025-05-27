package co.kr.my_portfolio.auth.controller;

import co.kr.my_portfolio.auth.dto.EmailLoginRequest;
import co.kr.my_portfolio.auth.dto.TokenResponse;
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
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    /**
     * 카카오 로그인 API
     */

    @PostMapping("/email")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithEmail(
            @Valid @RequestBody EmailLoginRequest request) {

        TokenResponse tokens = loginService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
