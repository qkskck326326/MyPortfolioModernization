package co.kr.my_portfolio.presentation.portfolioLike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioLikeResponse {
    private boolean liked;
    private int likeCount;

    public static PortfolioLikeResponse of(boolean liked, int likeCount) {
        return new PortfolioLikeResponse(liked, likeCount);
    }
}
