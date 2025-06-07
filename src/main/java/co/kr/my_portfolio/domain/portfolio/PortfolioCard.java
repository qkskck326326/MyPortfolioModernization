package co.kr.my_portfolio.domain.portfolio;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PortfolioCard {
    private final Long id;
    private final String thumbnail;
    private final String title;
    private final int likeCount;
    private final String nickname;
    private final LocalDateTime createdAt;

    @Builder
    @QueryProjection
    public PortfolioCard(Long id, String thumbnail, String title, int likeCount, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.title = title;
        this.likeCount = likeCount;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
