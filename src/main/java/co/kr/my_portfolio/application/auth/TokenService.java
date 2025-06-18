package co.kr.my_portfolio.application.auth;

import co.kr.my_portfolio.domain.auth.RefreshToken;
import co.kr.my_portfolio.domain.auth.RefreshTokenRepository;
import co.kr.my_portfolio.infrastructure.jwt.JwtAuthenticationToken;
import co.kr.my_portfolio.infrastructure.jwt.JwtProperties;
import co.kr.my_portfolio.infrastructure.jwt.JwtProvider;
import co.kr.my_portfolio.domain.user.Role;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.presentation.auth.dto.jwt.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
        Date expiration = jwtProvider.getExpiration(refreshToken);

        RefreshToken tokenEntity = RefreshToken.builder()
                .userId(Long.valueOf(userId))
                .token(refreshToken)
                .expiration(expiration.getTime())
                .build();

        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(accessToken, refreshToken);
    }

    // 로그아웃
    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        String userId = jwtProvider.getUserId(refreshToken);
        RefreshToken savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃된 사용자입니다."));

        if (Long.parseLong(userId) != savedToken.getUserId()) {
            throw new IllegalArgumentException("유저 불일치. 탈취 가능성 있음.");
        }

        refreshTokenRepository.deleteById(userId);
    }
    
    // 토큰 재생성
    @Transactional
    public TokenResponse reissueTokens(String refreshToken) {
        // 인증 객체에서 정보 꺼내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String userId = auth.getUserId();
        Role role = Role.valueOf(auth.getRole()); // 필요 시 enum 변환

        // DB에 저장된 리프레시 토큰 비교
        RefreshToken savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다."));

        if (!savedToken.isSameToken(refreshToken)) {
            throw new IllegalArgumentException("토큰 불일치. 탈취 가능성 있음.");
        }

        // AccessToken 재발급
        String newAccessToken = jwtProvider.createAccessToken(userId, role);

        // RefreshToken 재발급 필요 시
        if (savedToken.needsReissue(jwtProperties.getReissueThreshold())) {
            String newRefreshToken = jwtProvider.createRefreshToken(userId, role);
            Date expiration = jwtProvider.getExpiration(newRefreshToken);

            savedToken.updateToken(newRefreshToken, expiration.getTime());
            refreshTokenRepository.save(savedToken);

            return TokenResponse.of(newAccessToken, newRefreshToken);
        }

        return TokenResponse.of(newAccessToken, null);
    }

}