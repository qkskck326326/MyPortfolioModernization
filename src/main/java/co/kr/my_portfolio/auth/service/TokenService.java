package co.kr.my_portfolio.auth.service;

import co.kr.my_portfolio.auth.domain.RefreshToken;
import co.kr.my_portfolio.auth.repository.RefreshTokenRepository;
import co.kr.my_portfolio.global.jwt.JwtProperties;
import co.kr.my_portfolio.global.jwt.JwtProvider;
import co.kr.my_portfolio.user.domain.Role;
import co.kr.my_portfolio.user.domain.User;
import co.kr.my_portfolio.auth.dto.jwt.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    
    // 토큰 셋 반환
    public TokenResponse generateTokens(User user) {
        String userId = String.valueOf(user.getId());
        Role role = user.getRole();

        String accessToken = jwtProvider.createAccessToken(userId, role);
        String refreshToken = jwtProvider.createRefreshToken(userId, role);
        long expiration = jwtProvider.getExpiration(refreshToken);

        RefreshToken tokenEntity = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiration(expiration)
                .build();

        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(accessToken, refreshToken);
    }

    // 로그아웃
    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String userId = jwtProvider.getUserId(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃된 사용자입니다."));

        if (!savedToken.isSameToken(refreshToken)) {
            throw new IllegalArgumentException("토큰 불일치. 탈취 가능성 있음.");
        }

        refreshTokenRepository.deleteById(userId);
    }
    
    // 토큰 재생성
    public TokenResponse reissueTokens(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 유저 ID
        String userId = jwtProvider.getUserId(refreshToken);
        // Role
        Role role = jwtProvider.getRole(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다."));

        if (!savedToken.isSameToken(refreshToken)) {
            throw new IllegalArgumentException("토큰 불일치. 탈취 가능성 있음.");
        }

        String newAccessToken = jwtProvider.createAccessToken(userId, role);

        if (savedToken.needsReissue(jwtProperties.getReissueThreshold())) {
            String reissuedRefreshToken = jwtProvider.createRefreshToken(userId, role);
            Long expiration = jwtProvider.getExpiration(reissuedRefreshToken);

            savedToken.updateToken(reissuedRefreshToken, expiration);
            refreshTokenRepository.save(savedToken);

            return TokenResponse.of(newAccessToken, reissuedRefreshToken);
        }

        return TokenResponse.of(newAccessToken, null);
    }

}