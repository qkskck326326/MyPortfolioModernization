package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.QPortfolio;
import co.kr.my_portfolio.domain.portfolio.QPortfolioTagMapping;
import co.kr.my_portfolio.domain.tag.QTag;
import co.kr.my_portfolio.presentation.portfolio.dto.TagCountDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortfolioTagMappingQueryRepositoryImpl implements PortfolioTagMappingQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TagCountDto> countTagsByUserId(Long userId) {
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QPortfolio p = QPortfolio.portfolio;
        QTag t = QTag.tag;

        return queryFactory
                .select(Projections.constructor(TagCountDto.class,
                        t.name,
                        t.name.count().as("amount")
                ))
                .from(m)
                .join(m.portfolio, p)
                .join(m.tag, t)
                .where(p.author.id.eq(userId))
                .groupBy(t.name)
                .fetch();
    }
}
