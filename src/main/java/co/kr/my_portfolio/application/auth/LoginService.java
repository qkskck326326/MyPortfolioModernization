package co.kr.my_portfolio.application.auth;

import co.kr.my_portfolio.presentation.auth.dto.login.LoginRequest;
import co.kr.my_portfolio.application.auth.strategy.LoginStrategy;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final List<LoginStrategy> loginStrategies;
    private final TokenService tokenService;
    
    // 로그인
    public TokenResponse login(LoginRequest request) {
        LoginStrategy strategy = loginStrategies.stream()
                .filter(s -> s.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 방식입니다."));

        User user = strategy.authenticate(request);
        return tokenService.generateTokens(user);
    }
    
    // 로그아웃
    public void logout(String refreshToken) {
        tokenService.invalidateRefreshToken(refreshToken);

    }
    
    // 토큰 재생성
    public TokenResponse reissue(String refreshToken) {
        return tokenService.reissueTokens(refreshToken);
    }
}
