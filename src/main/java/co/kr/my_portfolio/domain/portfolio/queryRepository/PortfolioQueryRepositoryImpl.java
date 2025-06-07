package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.*;
import co.kr.my_portfolio.domain.portfolioLike.QPortfolioLike;
import co.kr.my_portfolio.domain.tag.QTag;
import co.kr.my_portfolio.domain.user.QUser;
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

    // 전체 포트폴리오 조회 (검색 + 태그 필터링 + 정렬 + 페이징)
    @Override
    public Page<PortfolioCard> getPortfolioCards(String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        // 키워드 조건 생성
        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }

        List<Long> portfolioIds;
        long total;

        // 태그 필터링이 있는 경우
        if (tagNames != null && !tagNames.isEmpty()) {
            JPAQuery<Long> countQuery = queryFactory
                    .select(p.id)
                    .from(p)
                    .join(p.tags, m)
                    .join(m.tag, t)
                    .where(condition.and(t.name.in(tagNames)))
                    .groupBy(p.id)
                    .having(t.name.countDistinct().eq((long) tagNames.size()));

            portfolioIds = countQuery
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = countQuery.fetch().size();
        } else {
            // 태그 필터링이 없는 경우
            portfolioIds = queryFactory
                    .select(p.id)
                    .from(p)
                    .where(condition)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .select(p.id.count())
                    .from(p)
                    .where(condition)
                    .fetchFirst();
        }

        // 결과가 없으면 빈 페이지 반환
        if (portfolioIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 실제 카드 조회
        List<PortfolioCard> content = queryFactory
                .select(new QPortfolioCard(p.id, p.thumbnail, p.title, p.likeCount, u.nickname, p.createdAt))
                .from(p)
                .join(p.author, u)
                .where(p.id.in(portfolioIds))
                .orderBy(getOrderSpecifiers(pageable.getSort()).toArray(new OrderSpecifier[0]))
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    // 내가 작성한 포트폴리오 조회
    @Override
    public Page<PortfolioCard> findMyPortfolios(String keyword, List<String> tagNames, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioTagMapping m = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        BooleanBuilder condition = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            condition.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }

        List<Long> portfolioIds;
        long total;

        // 태그 조건 분기 처리
        if (tagNames != null && !tagNames.isEmpty()) {
            portfolioIds = queryFactory
                    .select(p.id)
                    .from(p)
                    .join(p.tags, m)
                    .join(m.tag, t)
                    .where(condition.and(t.name.in(tagNames)))
                    .groupBy(p.id)
                    .having(t.name.countDistinct().eq((long) tagNames.size()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .select(p.id.countDistinct())
                    .from(p)
                    .join(p.tags, m)
                    .join(m.tag, t)
                    .where(condition.and(t.name.in(tagNames)))
                    .groupBy(p.id)
                    .having(t.name.countDistinct().eq((long) tagNames.size()))
                    .fetch()
                    .size();
        } else {
            portfolioIds = queryFactory
                    .select(p.id)
                    .from(p)
                    .where(condition)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory
                    .select(p.id.count())
                    .from(p)
                    .where(condition)
                    .fetchFirst();
        }

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

        return new PageImpl<>(content, pageable, total);
    }

    // 내가 좋아요 누른 포트폴리오 조회
    @Override
    public Page<PortfolioCard> findLikedPortfoliosByUserId(String keyword, List<String> tagNames, Long userId, Pageable pageable) {
        QPortfolio p = QPortfolio.portfolio;
        QUser u = QUser.user;
        QPortfolioLike pl = QPortfolioLike.portfolioLike;
        QPortfolioTagMapping ptm = QPortfolioTagMapping.portfolioTagMapping;
        QTag t = QTag.tag;

        // 기본 조건: 특정 유저의 좋아요
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(pl.user.id.eq(userId));
        if (keyword != null && !keyword.isBlank()) {
            builder.and(p.title.likeIgnoreCase("%" + keyword + "%"));
        }

        // 포트폴리오 카드 쿼리 생성
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

        // 총 개수 조회
        long total;
        if (tagNames != null && !tagNames.isEmpty()) {
            total = queryFactory
                    .select(p.id.countDistinct())
                    .from(pl)
                    .join(pl.portfolio, p)
                    .leftJoin(p.tags, ptm)
                    .leftJoin(ptm.tag, t)
                    .where(builder.and(t.name.in(tagNames)))
                    .fetch()
                    .size();
        } else {
            total = queryFactory
                    .select(p.id.countDistinct())
                    .from(pl)
                    .join(pl.portfolio, p)
                    .where(builder)
                    .fetch()
                    .size();
        }

        return new PageImpl<>(content, pageable, total);
    }

    // 정렬 조건을 OrderSpecifier 리스트로 변환
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