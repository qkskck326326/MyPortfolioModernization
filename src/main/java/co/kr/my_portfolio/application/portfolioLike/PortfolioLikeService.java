package co.kr.my_portfolio.application.portfolioLike;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolio.repository.PortfolioRepository;
import co.kr.my_portfolio.domain.portfolioLike.PortfolioLike;
import co.kr.my_portfolio.domain.portfolioLike.repository.PortfolioLikeRepository;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.global.exception.custom.PortfolioNotFoundException;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUser;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioLikeService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;


    // 포트폴리오 좋아요 표시 토글
    @Transactional
    public void portfolioLikeToggle(Long portfolioId) {
        // 비관적 락으로 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findByIdForUpdate(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오가 존재하지 않습니다."));
        
        // 인증 객체에서 userId 가져오기
        AuthenticatedUser authUser = authenticatedUserProvider.getAuthenticatedUser();
        Long userId = authUser.getId();

        // 연관관계 매핑을 위해 최소 정보(userId)로 User 객체 생성
        User user = User.withId(userId);

        // 좋아요 여부 확인 & 토글
        portfolioLikeRepository.findByUserAndPortfolio(user, portfolio)
                .ifPresentOrElse(
                        // 이미 좋아요한 상태라면 행 삭제 & count 감소
                        portfolioLike -> {
                            portfolioLikeRepository.delete(portfolioLike);
                            portfolio.decreaseLikeCount();
                        },
                        // 아니라면 행 추가 & count 증가
                        () -> {
                            portfolioLikeRepository.save(new PortfolioLike(user, portfolio));
                            portfolio.increaseLikeCount();
                        }
                );
    }
    
    
}
