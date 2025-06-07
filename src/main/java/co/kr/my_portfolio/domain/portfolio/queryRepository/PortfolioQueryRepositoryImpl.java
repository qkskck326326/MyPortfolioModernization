package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.*;
import co.kr.my_portfolio.domain.portfolioLike.QPortfolioLike;
import co.kr.my_portfolio.domain.tag.QTag;
import co.kr.my_portfolio.domain.user.QUser;
import co.kr.my_portfolio.presentation.portfolio.dto.search.SortRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.containsIgnoreCase(keyword).or(p.content.containsIgnoreCase(keyword)));
        }

        JPAQuery<Long> countQuery = queryFactory
                .select(p.id.countDistinct())
                .from(p)
                .join(p.tags, m)
                .join(m.tag, t)
                .where(condition.and(t.name.in(tagNames)))
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
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .fetch();

        long total = countQuery.fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PortfolioCard> findMyPortfolios(String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.containsIgnoreCase(keyword).or(p.content.containsIgnoreCase(keyword)));
        }

        BooleanBuilder tagCondition = new BooleanBuilder();
        if (tagNames != null && !tagNames.isEmpty()) {
            tagCondition.and(t.name.in(tagNames));
        }

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

        List<PortfolioCard> content = queryFactory
                .select(new QPortfolioCard(p.id, p.thumbnail, p.title, p.likeCount, p.author.nickname, p.createdAt))
                .from(p)
                .join(p.author, u)
                .where(p.id.in(portfolioIds))
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .fetch();

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

    @Override
    public Page<PortfolioCard> findLikedPortfoliosByUserId(String keyword, List<String> tagNames, Long userId, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioLike pl = QPortfolioLike.portfolioLike;
        QPortfolioTagMapping ptm = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(pl.user.id.eq(userId));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(p.title.containsIgnoreCase(keyword));
        }

        JPQLQuery<PortfolioCard> query = queryFactory
                .select(new QPortfolioCard(p.id, p.thumbnail, p.title, p.likeCount, u.nickname, p.createdAt))
                .from(pl)
                .join(pl.portfolio, p)
                .join(p.author, u)
                .leftJoin(ptm).on(ptm.portfolio.eq(p))
                .leftJoin(ptm.tag, t)
                .where(builder)
                .distinct();

        if (tagNames != null && !tagNames.isEmpty()) {
            query.where(t.name.in(tagNames));
        }

        List<PortfolioCard> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .fetch();

        long total = queryFactory
                .select(p.id.countDistinct())
                .from(pl)
                .join(pl.portfolio, p)
                .leftJoin(p.tags, ptm)
                .leftJoin(ptm.tag, t)
                .where(builder.and(tagNames != null && !tagNames.isEmpty() ? t.name.in(tagNames) : null))
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order order : sort) {
            PathBuilder<Portfolio> pathBuilder = new PathBuilder<>(Portfolio.class, "portfolio");
            Order direction = order.getDirection().isDescending() ? Order.DESC : Order.ASC;
            orderSpecifiers.add(new OrderSpecifier<>(direction, pathBuilder.getComparable(order.getProperty(), Comparable.class)));
        }
        return orderSpecifiers;
    }
}