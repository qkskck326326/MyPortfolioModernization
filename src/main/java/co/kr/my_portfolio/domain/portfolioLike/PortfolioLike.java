package co.kr.my_portfolio.domain.portfolioLike;

import co.kr.my_portfolio.common.domain.BaseTimeEntity;
import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "portfolio_id"})
})
public class PortfolioLike extends BaseTimeEntity {
    @EmbeddedId
    private PortfolioLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public PortfolioLike(User user, Portfolio portfolio) {
        this.portfolio = portfolio;
        this.user = user;
    }
}
