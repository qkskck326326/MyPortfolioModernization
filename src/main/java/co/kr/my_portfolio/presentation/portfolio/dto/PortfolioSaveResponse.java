package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "PortfolioSaveResponse - 포트폴리오 저장 응답 DTO",
        description = """
                포트폴리오 저장 응답을 위한 DTO 입니다.
                PortfolioId가 반환되고, 이것을 이용하여
                등록된 포트폴리오 페이지로 바로 이동 할 수 있습니다.
                """)
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
