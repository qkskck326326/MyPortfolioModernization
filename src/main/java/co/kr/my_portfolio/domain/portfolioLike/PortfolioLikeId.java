package co.kr.my_portfolio.domain.portfolioLike;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PortfolioLikeId implements Serializable {
    private Long userId;
    private Long portfolioId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PortfolioLikeId)) return false;
        PortfolioLikeId that = (PortfolioLikeId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(portfolioId, that.portfolioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, portfolioId);
    }
}