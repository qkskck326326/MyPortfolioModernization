package co.kr.my_portfolio.user.service;

import co.kr.my_portfolio.auth.repository.RefreshTokenRepository;
import co.kr.my_portfolio.auth.service.LoginService;
import co.kr.my_portfolio.global.exception.customException.UserNotFoundException;
import co.kr.my_portfolio.global.jwt.JwtAuthenticationToken;
import co.kr.my_portfolio.user.domain.Role;
import co.kr.my_portfolio.user.domain.User;
import co.kr.my_portfolio.user.dto.PasswordChangeRequest;
import co.kr.my_portfolio.user.dto.UserProfileResponse;
import co.kr.my_portfolio.user.dto.UserProfileUpdateRequest;
import co.kr.my_portfolio.user.dto.UserSignupRequest;
import co.kr.my_portfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .userThumbnail(request.getUserThumbnail())
                .introduce(request.getIntroduce())
                .github(request.getGithub())
                .birth(request.getBirth())
                .build();

        userRepository.save(user);
    }
    
    // 유저 프로필 반환
    public UserProfileResponse getMyInfo() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        String userId = (String) authentication.getPrincipal();

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UserProfileUpdateRequest request) {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Long userId = Long.parseLong((String) authentication.getPrincipal());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        user.updateProfile(
                request.getNickname(),
                request.getUserThumbnail(),
                request.getIntroduce(),
                request.getGithub(),
                request.getBirth()
        );

        return UserProfileResponse.from(user);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Long userId = Long.parseLong((String) authentication.getPrincipal());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        refreshTokenRepository.deleteByUserId(userId);
    }

}

