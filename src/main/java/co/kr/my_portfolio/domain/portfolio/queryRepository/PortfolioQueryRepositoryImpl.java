package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.*;
import co.kr.my_portfolio.domain.portfolioLike.QPortfolioLike;
import co.kr.my_portfolio.domain.tag.QTag;
import co.kr.my_portfolio.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
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
            condition.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }

        JPQLQuery<PortfolioCard> query = queryFactory
                .select(new QPortfolioCard(
                        p.id,
                        p.thumbnail,
                        p.title,
                        p.likeCount,
                        u.slug,
                        u.nickname,
                        p.createdAt))
                .from(p)
                .join(p.author, u)
                .where(condition);

        if (tagNames != null && !tagNames.isEmpty()) {
            query.join(p.tags, m)
                    .join(m.tag, t)
                    .where(t.name.in(tagNames))
                    .groupBy(p.id)
                    .having(t.name.countDistinct().eq((long) tagNames.size()));
        }

        long total = query.fetch().size();

        List<PortfolioCard> content = query
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PortfolioCard> findMyPortfolios(String slug, String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }
        if (slug != null && !slug.isBlank()) {
            condition.and(p.author.slug.eq(slug));
        }

        JPQLQuery<PortfolioCard> query = queryFactory
                .select(new QPortfolioCard(
                        p.id,
                        p.thumbnail,
                        p.title,
                        p.likeCount,
                        p.author.slug,
                        p.author.nickname,
                        p.createdAt))
                .from(p)
                .join(p.author, u)
                .where(condition);

        if (tagNames != null && !tagNames.isEmpty()) {
            query.join(p.tags, m)
                    .join(m.tag, t)
                    .where(t.name.in(tagNames))
                    .groupBy(p.id)
                    .having(t.name.countDistinct().eq((long) tagNames.size()));
        }

        long total = query.fetch().size();

        List<PortfolioCard> content = query
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

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
            builder.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }

        JPQLQuery<PortfolioCard> query = queryFactory
                .select(new QPortfolioCard(p.id, p.thumbnail, p.title, p.likeCount, u.slug, u.nickname, p.createdAt))
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

        long total = query.fetch().size();

        List<PortfolioCard> content = query
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort) {
        QPortfolio p = QPortfolio.portfolio;
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : sort) {
            Order direction = order.getDirection().isDescending() ? Order.DESC : Order.ASC;

            switch (order.getProperty()) {
                case "createdAt" -> {
                    orderSpecifiers.add(new OrderSpecifier<>(direction, p.createdAt));
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, p.id));
                }
                case "likeCount" -> orderSpecifiers.add(new OrderSpecifier<>(direction, p.likeCount));
                default -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, p.createdAt));
            }
        }

        return orderSpecifiers;
    }
}
