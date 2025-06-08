package co.kr.my_portfolio.presentation.portfolioLike.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Schema(name = "PortfolioLikeToggleRequest - 포트폴리오 좋아요 토글 요청 DTO", description = """
        포트폴리오 좋아요 토글 요청 DTO 입니다.
        """)
@Getter
public class PortfolioLikeToggleRequest {

    @Schema(description = "포트폴리오의 아이디", example = "1")
    @NotNull(message = "포트폴리오 ID는 필수입니다.")
    @Positive(message = "유효한 포트폴리오 ID를 입력해주세요.")
    private Long portfolioId;
}
