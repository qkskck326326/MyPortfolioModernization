package co.kr.my_portfolio.presentation.user.controller;

import co.kr.my_portfolio.global.response.CommonResponse;
import co.kr.my_portfolio.infrastructure.jwt.JwtAuthenticationToken;
import co.kr.my_portfolio.presentation.portfolio.dto.EmailCheckRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.NicknameCheckRequest;
import co.kr.my_portfolio.presentation.user.dto.*;
import co.kr.my_portfolio.application.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원 정보 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "이메일 회원가입", description = """
            입력된 정보로 회원가입 합니다.
            API 사용 조건
           - email, password, nickname 생략 불가능
                - email : email 형식이여야 합니다.
                - password : 8자 ~ 16자 이상이어야 합니다.
                - nickname : 최대 16자 까지 가능합니다.
            - userThumbnail, introduce, github, birth 생략 가능
                - userThumbnail : 최대 500자까지 가능합니다.
                - introduce : 최대 2000자까지 가능합니다.
                - github : 최대 100자까지 가능합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류 또는 중복된 이메일")
    })
    @PostMapping("/signup/email/public")
    public ResponseEntity<CommonResponse<String>> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(CommonResponse.success(null, "회원가입 완료"));
    }

    @PostMapping("/check-nickname/public")
    public ResponseEntity<CommonResponse<Boolean>> checkNickname(
            @Valid @RequestBody NicknameCheckRequest request
    ) {
        boolean exists = userService.existsByNickname(request.getNickname());
        return ResponseEntity.ok(CommonResponse.success(exists));
    }

    @PostMapping("/check-email/public")
    public ResponseEntity<CommonResponse<Boolean>> checkEmail(
            @Valid @RequestBody EmailCheckRequest request
    ) {
        boolean exists = userService.existsByEmail(request.getEmail());
        return ResponseEntity.ok(CommonResponse.success(exists));
    }

    // 프론트에서 user 정보 store 저장을 위한 api
    @GetMapping("/my/info")
    public ResponseEntity<CommonResponse<UserInfoResponse>> getMyInfo() {
        UserInfoResponse response = userService.getMyInfoForState();
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    // 특정 유저 정보 조회 api
    @GetMapping("/{slug}/info/public")
    public ResponseEntity<CommonResponse<UserProfileAndTagsResponse>> getUserInfoBySlug(@PathVariable String slug) {
        UserProfileAndTagsResponse response = userService.getUserInfoBySlug(slug);
        return ResponseEntity.ok(CommonResponse.success(response));
    }



    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내 정보 조회", description = """
            JWT 인증을 통해 본인의 프로필 정보를 조회합니다.
            API 사용 조건
            - JWT 인증( 로그인 )
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getMyPage(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        UserProfileResponse response = userService.getMyInfo();
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내 정보 수정", description = """
            JWT 인증을 통해 본인의 프로필 정보를 수정합니다.
            API 사용 조건
            - JWT 인증( 로그인 )
            - nickname 생략 불가능
                - nickname : 최대 16자 까지 가능합니다.
            - userThumbnail, introduce, github, birth 생략 가능
                - userThumbnail : 최대 500자까지 가능합니다.
                - introduce : 최대 2000자까지 가능합니다.
                - github : 최대 100자까지 가능합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PutMapping("/me")
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "비밀번호 변경", description = """
            기존 비밀번호와 새로운 비밀번호를 입력해 비밀번호를 변경합니다.
            API 사용 조건
            - JWT 인증( 로그인 )
            - currentPassword, newPassword 생략 불가능
                - currentPassword : 기존에 사용하던 비밀번호를 입력하여야 합니다.
                - newPassword : 변경 할 비밀번호를 입력하여야 합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "기존 비밀번호 불일치 또는 형식 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PutMapping("/me/password")
    public ResponseEntity<CommonResponse<?>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(CommonResponse.success(null, "비밀번호가 성공적으로 변경되었습니다. \n 다시 로그인해주세요."));
    }

}

