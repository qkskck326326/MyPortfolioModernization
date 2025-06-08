package co.kr.my_portfolio.presentation.portfolio.dto;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "PortfolioDetailResponse - 포트폴리오 상세 응답 DTO", description = """
        특정 포트폴리오의 내용 전반과 작성자 닉네임,
        속한 테그들을 반환합니다.
        """)
@Getter
public class PortfolioDetailResponse {
    @Schema(description = "포트폴리오 ID", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "포트폴리오 제작 웹사이트")
    private final String title;

    @Schema(description = "내용", example = "포트폴리오 게제 사이트를 기획하게 되었습니다.")
    private final String content;

    @Schema(description = "썸네일 URL", example = "https://example.com/image.jpg")
    private final String thumbnail;

    @Schema(description = "작성자 Nickname", example = "이건우")
    private final String authorNickname;

    @Schema(description = "좋아요 수", example = "42")
    private final int likeCount;

    @Schema(description = "포트폴리오에 속한 테그들", example = "[\"AI\", \"SpringBoot\"]")
    private final List<String> tags;

    @Builder
    private PortfolioDetailResponse(Long id, String title, String content, String thumbnail,
                                    String authorNickname, int likeCount, List<String> tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.authorNickname = authorNickname;
        this.likeCount = likeCount;
        this.tags = tags;
    }

    public static PortfolioDetailResponse of(Portfolio portfolio) {
        return PortfolioDetailResponse.builder()
                .id(portfolio.getId())
                .title(portfolio.getTitle())
                .content(portfolio.getContent())
                .thumbnail(portfolio.getThumbnail())
                .authorNickname(portfolio.getAuthor().getNickname())
                .likeCount(portfolio.getLikeCount())
                .tags(
                        portfolio.getTags().stream()
                                .map(mapping -> mapping.getTag().getName())
                                .toList()
                )
                .build();
    }
}
