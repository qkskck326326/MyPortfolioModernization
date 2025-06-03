package co.kr.my_portfolio.presentation.portfolio.dto;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioDetailResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String thumbnail;
    private final String authorNickname;
    private final int likeCount;
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
