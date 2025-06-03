package co.kr.my_portfolio.domain.portfolio;

import co.kr.my_portfolio.domain.tag.Tag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioTagMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Tag tag;

    public static PortfolioTagMapping of(Portfolio portfolio, Tag tag) {
        PortfolioTagMapping mapping = new PortfolioTagMapping();
        mapping.portfolio = portfolio;
        mapping.tag = tag;
        return mapping;
    }
}
