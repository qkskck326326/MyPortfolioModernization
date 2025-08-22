package co.kr.my_portfolio.application.user;

import co.kr.my_portfolio.domain.portfolio.queryRepository.PortfolioTagMappingQueryRepository;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUser;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.domain.auth.RefreshTokenRepository;
import co.kr.my_portfolio.global.exception.custom.UserNotFoundException;
import co.kr.my_portfolio.domain.user.Role;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.infrastructure.user.SlugGenerator;
import co.kr.my_portfolio.presentation.portfolio.dto.TagCountDto;
import co.kr.my_portfolio.presentation.user.dto.*;
import co.kr.my_portfolio.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final SlugGenerator slugGenerator;
    private final PortfolioTagMappingQueryRepository portfolioTagMappingQueryRepository;

    // 회원가입
    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .slug(slugGenerator.generate(request.getNickname()))
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
    public UserProfileResponse  getMyInfo() {
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

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserInfoResponse getMyInfoForState() {
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();
        Long userId = authenticatedUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        return UserInfoResponse.builder()
                .nickname(user.getNickname())
                .slug(user.getSlug())
                .build();
    }

    @Transactional
    public UserProfileAndTagsResponse getUserInfoBySlug(String slug) {
        User user = userRepository.findBySlug(slug);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
        }
        List<TagCountDto> tags = portfolioTagMappingQueryRepository.countTagsByUserId(user.getId());
        return UserProfileAndTagsResponse.builder()
                .userThumbnail(user.getUserThumbnail())
                .github(user.getGithub())
                .introduce(user.getIntroduce())
                .slug(user.getSlug())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .tags(tags)
                .build();
    }
}

