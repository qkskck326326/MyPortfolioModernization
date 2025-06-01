package co.kr.my_portfolio.application.auth.strategy;

import co.kr.my_portfolio.presentation.auth.dto.login.EmailLoginRequest;
import co.kr.my_portfolio.presentation.auth.dto.login.LoginRequest;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailLoginStrategy implements LoginStrategy {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(LoginRequest request) {
        return request instanceof EmailLoginRequest;
    }

    @Override
    public User authenticate(LoginRequest request) {
        EmailLoginRequest emailRequest = (EmailLoginRequest) request;

        User user = userRepository.findByEmail(emailRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(emailRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
