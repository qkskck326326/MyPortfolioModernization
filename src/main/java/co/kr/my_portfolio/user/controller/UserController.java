package co.kr.my_portfolio.user.controller;

import co.kr.my_portfolio.common.dto.ApiResponse;
import co.kr.my_portfolio.global.jwt.JwtAuthenticationToken;
import co.kr.my_portfolio.user.dto.PasswordChangeRequest;
import co.kr.my_portfolio.user.dto.UserProfileResponse;
import co.kr.my_portfolio.user.dto.UserProfileUpdateRequest;
import co.kr.my_portfolio.user.dto.UserSignupRequest;
import co.kr.my_portfolio.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody @Valid UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(null, "회원가입 완료"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyPage(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        UserProfileResponse response = userService.getMyInfo();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "비밀번호가 성공적으로 변경되었습니다. \n 다시 로그인해주세요."));
    }
}

