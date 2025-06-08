package co.kr.my_portfolio.presentation.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PortfolioSaveResponse {
    Long PortfolioId;

    @Builder
    public PortfolioSaveResponse(Long portfolioId) {
        this.PortfolioId = portfolioId;
    }

    public static PortfolioSaveResponse of(long portfolioId) {
        return PortfolioSaveResponse.builder()
                .portfolioId(portfolioId)
                .build();
    }
}
