package co.kr.my_portfolio.application.auth.strategy;

import co.kr.my_portfolio.application.auth.dto.EmailLoginCommand;
import co.kr.my_portfolio.application.auth.dto.LoginCommand;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.domain.user.UserRepository;
import co.kr.my_portfolio.global.exception.custom.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailLoginStrategy implements LoginStrategy {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(LoginCommand request) {
        return request instanceof EmailLoginCommand;
    }

    @Override
    public User authenticate(LoginCommand request) {
        EmailLoginCommand emailRequest = (EmailLoginCommand) request;

        User user = userRepository.findByEmail(emailRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(emailRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
