package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "포트폴리오 등록 요청 DTO")
@Getter
@NoArgsConstructor
public class PortfolioSaveRequest {
    @Schema(description = "제목", example = "MP 프로젝트 포트폴리오")
    @NotBlank(message = "제목이 입력되지 않았습니다.")
    @Size(max = 100, message = "제목이 너무 깁니다.")
    private String title;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    @Size(max = 255, message = "썸네일 경로는 최대 255자까지 입력할 수 있습니다.")
    private String thumbnail;

    @Schema(description = "상세 설명", example = "이 프로젝트는 포트폴리오를 등록/색인 하는 웹사이트 제작 프로젝트 입니다.")
    @NotBlank(message = "내용이 입력되지 않았습니다.")
    @Size(max = 10000, message = "내용은 최대 10000자까지 입력할 수 있습니다.")
    private String content;

    @Schema(description = "태그 목록", example = "[\"AI\", \"SpringBoot\"]")
    private List<String> tags;
}
