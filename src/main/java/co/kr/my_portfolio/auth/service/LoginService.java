package co.kr.my_portfolio.auth.service;

import co.kr.my_portfolio.auth.dto.LoginRequest;
import co.kr.my_portfolio.auth.strategy.LoginStrategy;
import co.kr.my_portfolio.user.domain.User;
import co.kr.my_portfolio.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final List<LoginStrategy> loginStrategies;
    private final TokenService tokenService;

    public TokenResponse login(LoginRequest request) {
        LoginStrategy strategy = loginStrategies.stream()
                .filter(s -> s.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 방식입니다."));

        User user = strategy.authenticate(request);
        return tokenService.generateTokens(user);
    }
}
