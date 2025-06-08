package co.kr.my_portfolio.presentation.portfolio.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Schema(name = "SortRequest - 포트폴리오 검색 기준",
        description = """
                포트폴리오 검색 기준입니다.
                """)
@Getter
@NoArgsConstructor
public class SortRequest {
    @Schema(description = "검색 기준", example = "likeCount")
    private String field = "createAt";
    @Schema(description = "검색 정렬", example = "DESC")
    private Sort.Direction direction = Sort.Direction.DESC;
}
