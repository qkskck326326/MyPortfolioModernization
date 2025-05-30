package co.kr.my_portfolio.user.service;

import co.kr.my_portfolio.auth.service.LoginService;
import co.kr.my_portfolio.user.domain.Role;
import co.kr.my_portfolio.user.domain.User;
import co.kr.my_portfolio.user.dto.UserSignupRequest;
import co.kr.my_portfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;

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

}

