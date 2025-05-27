package co.kr.my_portfolio.user.controller;

import co.kr.my_portfolio.common.dto.ApiResponse;
import co.kr.my_portfolio.auth.dto.UserSignupRequest;
import co.kr.my_portfolio.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody @Valid UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 완료"));
    }
}

