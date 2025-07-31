package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "태그별 사용 개수 DTO")
public class TagCountDto {

    @Schema(description = "태그 이름", example = "Spring")
    private String name;

    @Schema(description = "사용 횟수", example = "4")
    private long amount;

    public TagCountDto(String name, long amount) {
        this.name = name;
        this.amount = amount;
    }
}
