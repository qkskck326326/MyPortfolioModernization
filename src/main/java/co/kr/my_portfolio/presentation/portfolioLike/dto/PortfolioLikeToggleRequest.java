package co.kr.my_portfolio.presentation.portfolioLike.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class PortfolioLikeToggleRequest {

    @NotNull(message = "포트폴리오 ID는 필수입니다.")
    @Positive(message = "유효한 포트폴리오 ID를 입력해주세요.")
    private Long portfolioId;
}
