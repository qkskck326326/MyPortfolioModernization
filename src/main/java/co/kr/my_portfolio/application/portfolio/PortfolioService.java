package co.kr.my_portfolio.application.portfolio;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolio.repository.PortfolioRepository;
import co.kr.my_portfolio.domain.tag.Tag;
import co.kr.my_portfolio.domain.tag.repository.TagRepository;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TagRepository tagRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public void savePortfolio(PortfolioSaveRequest request) {
        Long userId = authenticatedUserProvider.getAuthenticatedUser().getId();

    }

    @Transactional
    public void updatePortfolioAndTags(PortfolioUpdateRequest request) {
        Portfolio portfolio = portfolioRepository.findWithTagsById(request.getPortfolioId());
        portfolio.updateWithoutTags(request);

        List<String> tagNames = request.getTags();

        List<Tag> resolvedTags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.of(name)))
                ).toList();

        portfolio.syncTags(resolvedTags);

        portfolioRepository.save(portfolio);
    }
}