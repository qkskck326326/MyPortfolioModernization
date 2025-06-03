package co.kr.my_portfolio.domain.portfolio;

import co.kr.my_portfolio.common.domain.BaseTimeEntity;
import co.kr.my_portfolio.domain.tag.Tag;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    private String title;
    private String thumbnail;

    @Lob
    private String content;

    private Integer likeCount = 0;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<PortfolioTagMapping> tags = new ArrayList<>();

    public static Portfolio of(PortfolioSaveRequest request, User author) {
        Portfolio portfolio = new Portfolio();
        portfolio.author = author;
        portfolio.title = request.getTitle();
        portfolio.thumbnail = request.getThumbnail();
        portfolio.content = request.getContent();
        return portfolio;
    }

    public void updateWithoutTags(PortfolioUpdateRequest request) {
        this.title = request.getTitle();
        this.thumbnail = request.getThumbnail();
        this.content = request.getContent();
    }

    public void addTag(Tag tag) {
        PortfolioTagMapping mapping = PortfolioTagMapping.of(this, tag);
        tags.add(mapping);
    }

    public void removeTag(PortfolioTagMapping mapping) {
        tags.remove(mapping);
    }

    public List<Tag> extractTagsToAdd(List<Tag> newTags) {
        List<String> existingTagNames = tags.stream()
                .map(m -> m.getTag().getName())
                .toList();

        return newTags.stream()
                .filter(tag -> !existingTagNames.contains(tag.getName()))
                .toList();
    }

    public List<PortfolioTagMapping> extractMappingsToRemove(List<Tag> newTags) {
        List<String> newTagNames = newTags.stream()
                .map(Tag::getName)
                .toList();

        return tags.stream()
                .filter(mapping -> !newTagNames.contains(mapping.getTag().getName()))
                .toList();
    }

    public void syncTags(List<Tag> newTags) {
        List<Tag> toAdd = extractTagsToAdd(newTags);
        List<PortfolioTagMapping> toRemove = extractMappingsToRemove(newTags);

        toRemove.forEach(this::removeTag);
        toAdd.forEach(this::addTag);
    }
}