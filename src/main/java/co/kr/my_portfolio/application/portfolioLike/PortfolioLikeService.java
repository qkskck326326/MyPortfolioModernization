package co.kr.my_portfolio.application.portfolioLike;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolio.repository.PortfolioRepository;
import co.kr.my_portfolio.domain.portfolioLike.PortfolioLike;
import co.kr.my_portfolio.domain.portfolioLike.repository.PortfolioLikeRepository;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.domain.user.UserRepository;
import co.kr.my_portfolio.global.exception.custom.PortfolioNotFoundException;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUser;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.presentation.portfolioLike.dto.PortfolioLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class PortfolioLikeService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;


    // 포트폴리오 좋아요 표시 토글
    @Transactional
    public PortfolioLikeResponse portfolioLikeToggle(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdForUpdate(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오가 존재하지 않습니다."));

        AuthenticatedUser authUser = authenticatedUserProvider.getAuthenticatedUser();
        User user = userRepository.getReferenceById(authUser.getId());

        boolean liked;
        Optional<PortfolioLike> optionalLike = portfolioLikeRepository.findByUserAndPortfolio(user, portfolio);

        if (optionalLike.isPresent()) {
            // 좋아요 상태 -> 삭제
            portfolioLikeRepository.delete(optionalLike.get());
            portfolio.decreaseLikeCount();
            liked = false;
        } else {
            // 좋아요 상태 X -> 추가
            portfolioLikeRepository.save(new PortfolioLike(user, portfolio));
            portfolio.increaseLikeCount();
            liked = true;
        }

        return PortfolioLikeResponse.of(liked, portfolio.getLikeCount());
    }

    // 특정 포트폴리오를 특정 유저가 좋아요 표시 하였는지 확인
    @Transactional
    public boolean portfolioLikeCheck(Long portfolioId) {
        // 비관적 락으로 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findByIdForUpdate(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오가 존재하지 않습니다."));

        // 인증 객체에서 userId 가져오기
        AuthenticatedUser authUser = authenticatedUserProvider.getAuthenticatedUser();
        Long userId = authUser.getId();

        // 연관관계 매핑을 위해 최소 정보(userId)로 User 객체 생성
        User user = User.withId(userId);
        
        // 존재여부 그대로 반환
        return portfolioLikeRepository.existsByPortfolioAndUser(portfolio, user);
    }
}
