package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.PortfolioCard;

import co.kr.my_portfolio.domain.portfolio.QPortfolio;
import co.kr.my_portfolio.domain.portfolio.QPortfolioCard;
import co.kr.my_portfolio.domain.portfolio.QPortfolioTagMapping;
import co.kr.my_portfolio.domain.tag.QTag;
import co.kr.my_portfolio.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortfolioQueryRepositoryImpl implements PortfolioQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PortfolioCard> getPortfolioCards(String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        // 기본 조건
        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.containsIgnoreCase(keyword)
                    .or(p.content.containsIgnoreCase(keyword)));
        }

        // Tag 검색: 태그 이름 목록을 포함하는 Portfolio 만 필터링
        JPAQuery<Long> countQuery = queryFactory
                .select(p.id.countDistinct())
                .from(p)
                .join(p.tags, m)
                .join(m.tag, t)
                .where(condition
                        .and(t.name.in(tagNames)))
                .groupBy(p.id)
                .having(t.name.countDistinct().eq((long) tagNames.size()));

        List<Long> portfolioIds = countQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PortfolioCard> content = queryFactory
                .select(new QPortfolioCard(p.id, p.thumbnail, p.title, p.likeCount, u.nickname, p.createdAt))
                .from(p)
                .join(p.author, u)
                .where(p.id.in(portfolioIds))
                .fetch();

        long total = (long) countQuery.fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PortfolioCard> findMyPortfolios(String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        // 검색 조건
        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(
                    p.title.containsIgnoreCase(keyword)
                            .or(p.content.containsIgnoreCase(keyword))
            );
        }

        // 태그 조건 (AND 포함)
        BooleanBuilder tagCondition = new BooleanBuilder();
        if (tagNames != null && !tagNames.isEmpty()) {
            tagCondition.and(t.name.in(tagNames));
        }

        // ID 쿼리: 조건에 맞는 포트폴리오 ID만 추출
        List<Long> portfolioIds = queryFactory
                .select(p.id)
                .from(p)
                .join(p.tags, m)
                .join(m.tag, t)
                .where(condition.and(tagCondition))
                .groupBy(p.id)
                .having(t.name.countDistinct().eq((long) tagNames.size()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (portfolioIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 본문 조회: ID 기반 + User nickname 포함
        List<PortfolioCard> content = queryFactory
                .select(new QPortfolioCard(
                        p.id,
                        p.thumbnail,
                        p.title,
                        p.likeCount,
                        p.author.nickname,
                        p.createdAt
                ))
                .from(p)
                .join(p.author, u)
                .where(p.id.in(portfolioIds))
                .orderBy(p.createdAt.desc())
                .fetch();

        // 전체 개수
        long total = queryFactory
                .select(p.id.countDistinct())
                .from(p)
                .join(p.tags, m)
                .join(m.tag, t)
                .where(condition.and(tagCondition))
                .groupBy(p.id)
                .having(t.name.countDistinct().eq((long) tagNames.size()))
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }
}
