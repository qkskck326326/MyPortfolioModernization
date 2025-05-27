package co.kr.my_portfolio.auth.service;

import co.kr.my_portfolio.global.jwt.JwtProvider;
import co.kr.my_portfolio.user.domain.User;
import co.kr.my_portfolio.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;

    public TokenResponse generateTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(String.valueOf(user.getId()), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(String.valueOf(user.getId()), user.getRole());
        return TokenResponse.of(accessToken, refreshToken);
    }
}
