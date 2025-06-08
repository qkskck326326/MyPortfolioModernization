package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포트폴리오 저장 응답 DTO")
@Getter
public class PortfolioSaveResponse {
    @Schema(description = "포트폴리오 ID", example = "1")
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
