package co.kr.my_portfolio.application.user;

import co.kr.my_portfolio.infrastructure.security.AuthenticatedUser;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.domain.auth.RefreshTokenRepository;
import co.kr.my_portfolio.global.exception.custom.UserNotFoundException;
import co.kr.my_portfolio.domain.user.Role;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.presentation.user.dto.PasswordChangeRequest;
import co.kr.my_portfolio.presentation.user.dto.UserProfileResponse;
import co.kr.my_portfolio.presentation.user.dto.UserProfileUpdateRequest;
import co.kr.my_portfolio.presentation.user.dto.UserSignupRequest;
import co.kr.my_portfolio.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    // 회원가입
    @Transactional
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
    @Transactional
    public UserProfileResponse getMyInfo() {
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        Long userId = authenticatedUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        return UserProfileResponse.from(user);
    }
    
    // 유저 프로필 업데이트
    @Transactional
    public UserProfileResponse updateMyProfile(UserProfileUpdateRequest request) {
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        Long userId = authenticatedUser.getId();

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
    
    // 유저 비밀번호 수정
    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();

        Long userId = authenticatedUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        refreshTokenRepository.deleteByUserId(userId);
    }

}

