package co.kr.my_portfolio.presentation.portfolio.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "PortfolioSearchRequest - 포트폴리오 검색 요청 DTO",
        description = """
                포트폴리오 검색 요청을 위한 DTO 입니다.
                """)
@Getter
@NoArgsConstructor
public class PortfolioSearchRequest {

    @Schema(description = "검색 키워드", example = "프로젝트")
    private String keyword;

    @Schema(description = "검색 테그들", example = "[\"AI\", \"SpringBoot\"]")
    private List<String> tags;

    @Schema(description = "현재 페이지", example = "0")
    private int page = 0;

    @Schema(description = "페이지당 갯수", example = "15")
    private int size = 15;

    @Schema(description = "정렬 기준")
    private List<SortRequest> sort = new ArrayList<>();

    public PortfolioSearchRequest(String keyword, List<String> tags, int page, int size, List<SortRequest> sort) {
        this.keyword = keyword;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.page = page;
        this.size = size;
        this.sort = sort != null ? sort : new ArrayList<>();
    }
}
