package co.kr.my_portfolio.domain.portfolio.queryRepository;

import co.kr.my_portfolio.presentation.portfolio.dto.TagCountDto;

import java.util.List;

public interface PortfolioTagMappingQueryRepository {
    List<TagCountDto> countTagsByUserId(Long userId);
}
