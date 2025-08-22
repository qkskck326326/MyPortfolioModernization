package co.kr.my_portfolio.domain.portfolioLike.repository;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolioLike.PortfolioLike;
import co.kr.my_portfolio.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioLikeRepository extends JpaRepository<PortfolioLike, Long> {
    Optional<PortfolioLike> findByUserAndPortfolio(User user, Portfolio portfolio);
    boolean existsByUserAndPortfolio(User user, Portfolio portfolio);
    void deleteByUserAndPortfolio(User user, Portfolio portfolio);

    boolean existsByPortfolioAndUser(Portfolio portfolio, User user);
}