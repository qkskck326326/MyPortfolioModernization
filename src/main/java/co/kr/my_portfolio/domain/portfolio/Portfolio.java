package co.kr.my_portfolio.domain.portfolio;

import co.kr.my_portfolio.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio")
public class Portfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userPid;

    private String userNickname;

    private String thumbnail;

    private String title;

    @Lob
    private String content;

    private Integer likeCount = 0;

    @ElementCollection
    @CollectionTable(
            name = "portfolio_tags",
            joinColumns = @JoinColumn(name = "portfolio_id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    protected Portfolio() {} // JPA용

    @Builder
    public Portfolio(Long userPid, String userNickname, String thumbnail,
                     String title, String content, List<String> tags) {
        this.userPid = userPid;
        this.userNickname = userNickname;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public void upLikeCount() {
        this.likeCount++;
    }

    public void downLikeCount() {
        this.likeCount--;
    }

    public void updateContent(String title, String content, List<String> newTags) {
        this.title = title;
        this.content = content;
        this.tags = newTags;
    }


}
