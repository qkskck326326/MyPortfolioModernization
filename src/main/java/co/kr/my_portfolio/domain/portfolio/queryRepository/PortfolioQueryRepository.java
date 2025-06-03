package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.domain.portfolio.PortfolioCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortfolioQueryRepository {
    Page<PortfolioCard> getPortfolioCards(String keyword, List<String> tagNames, Pageable pageable);
}
