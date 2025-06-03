package co.kr.my_portfolio.presentation.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PortfolioUpdateRequest {
    @NotBlank(message = "portfolioId가 전달되지 않았습니다.")
    private Long portfolioId;

    @NotBlank(message = "제목이 입력되지 않았습니다.")
    @Size(max = 100, message = "제목이 너무 깁니다.")
    private String title;

    @Size(max = 255, message = "썸네일 경로는 최대 255자까지 입력할 수 있습니다.")
    private String thumbnail;

    @NotBlank(message = "내용이 입력되지 않았습니다.")
    @Size(max = 10000, message = "내용은 최대 10000자까지 입력할 수 있습니다.")
    private String content;

    private List<String> tags;
}
