package co.kr.my_portfolio.domain.portfolio;

import com.querydsl.core.annotations.QueryProjection;

public record PortfolioCard(Long id, String thumbnail, String title, int likeCount, String nickname) {
    @QueryProjection
    public PortfolioCard {
    }
}
