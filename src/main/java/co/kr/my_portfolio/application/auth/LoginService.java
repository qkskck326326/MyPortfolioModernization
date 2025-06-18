package co.kr.my_portfolio.application.auth;

import co.kr.my_portfolio.application.auth.dto.LoginCommand;
import co.kr.my_portfolio.application.auth.strategy.LoginStrategy;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.global.exception.custom.UnauthorizedException;
import co.kr.my_portfolio.infrastructure.jwt.JwtProvider;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUser;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final List<LoginStrategy> loginStrategies;
    private final TokenService tokenService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final JwtProvider jwtProvider;

    // 로그인
    public TokenResponse login(LoginCommand request) {
        LoginStrategy strategy = loginStrategies.stream()
                .filter(s -> s.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 방식입니다."));

        User user = strategy.authenticate(request);
        return tokenService.generateTokens(user);
    }
    
    // 로그아웃
    public void logout(String refreshToken) {
        // 인증 객체에서 인증 유저 정보 꺼내기
        AuthenticatedUser authenticatedUser = authenticatedUserProvider.getAuthenticatedUser();
        // 인증 유저의 id 꺼내기
        Long authUserId = authenticatedUser.getId();
        
        // null 체크 후
        if (authUserId == null) {
            throw new UnauthorizedException("인증 정보가 유효하지 않습니다. 다시 로그인해주세요.");
        }
        
        // 들어온 refreshToken 안의 UserId 와 일치하지 않는다면
        if (authUserId != Long.parseLong(jwtProvider.getUserId(refreshToken))) {
            throw new UnauthorizedException("사용자 정보가 일치하지 않습니다.");
        }

        tokenService.invalidateRefreshToken(refreshToken);
    }
    
    // 토큰 재생성
    public TokenResponse reissue(String refreshToken) {
        return tokenService.reissueTokens(refreshToken);
    }
}
