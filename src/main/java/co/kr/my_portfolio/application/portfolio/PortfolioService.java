package co.kr.my_portfolio.application.portfolio;

import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolio.PortfolioCard;
import co.kr.my_portfolio.domain.portfolio.queryRepository.PortfolioQueryRepositoryImpl;
import co.kr.my_portfolio.domain.portfolio.repository.PortfolioRepository;
import co.kr.my_portfolio.domain.tag.Tag;
import co.kr.my_portfolio.domain.tag.repository.TagRepository;
import co.kr.my_portfolio.domain.user.User;
import co.kr.my_portfolio.domain.user.UserRepository;
import co.kr.my_portfolio.global.exception.custom.PortfolioNotFoundException;
import co.kr.my_portfolio.global.exception.custom.UserNotFoundException;
import co.kr.my_portfolio.infrastructure.security.AuthenticatedUserProvider;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioDetailResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.search.PortfolioSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TagRepository tagRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PortfolioQueryRepositoryImpl portfolioQueryRepositoryImpl;
    private final UserRepository userRepository;

    // 포트폴리오 등록
    @Transactional
    public void savePortfolio(PortfolioSaveRequest request) {
        // 유저 아이디 가져오기
        Long userId = authenticatedUserProvider.getAuthenticatedUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
        
        // request, user로 Portfolio 생성
        Portfolio portfolio = Portfolio.of(request, user);
        
        // 들어온 테그 이름 리스트 추출
        List<String> tagNames = request.getTags();
        
        // 들어온 테그 -> 테그 도메인 리스트로 변환 + 없는 테그 등록
        List<Tag> resolvedTags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.of(name)))
                ).toList();

        // 테그 매핑 들어온 테그 도메인 리스트와 일치시키기
        portfolio.syncTags(resolvedTags);
        
        // 포트폴리오 저장
        portfolioRepository.save(portfolio);
    }
    
    // 포트폴리오 수정
    @Transactional
    public void updatePortfolioAndTags(PortfolioUpdateRequest request) {
        // 포트폴리오 id로 포트폴리오 불러오기
        Portfolio portfolio = portfolioRepository.findWithTagsById(request.getPortfolioId());
        
        // request 로 Tag 외의 내용으로 수정
        portfolio.updateWithoutTags(request);

        // 들어온 테그 이름 리스트 추출
        List<String> tagNames = request.getTags();

        // 들어온 테그 -> 테그 도메인 리스트로 변환 + 없는 테그 등록
        List<Tag> resolvedTags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.of(name)))
                ).toList();
        
        // 테그 매핑 들어온 테그 도메인 리스트와 일치시키기
        portfolio.syncTags(resolvedTags);

        // 포트폴리오 저장
        portfolioRepository.save(portfolio);
    }
    
    // 포트폴리오 삭제
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        portfolioRepository.deleteById(portfolioId);
    }

    // 포트폴리오 가져오기 (Card + Pagination)
    @Transactional
    public Page<PortfolioCard> getPortfolioCards(PortfolioSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(request.sort().stream()
                        .map(s -> s.direction().equalsIgnoreCase("DESC")
                                ? Sort.Order.desc(s.property())
                                : Sort.Order.asc(s.property()))
                        .toList())
        );

        return portfolioQueryRepositoryImpl.getPortfolioCards(request.keyword(), request.tags(), pageable);
    }

    // 내가 쓴 글 목록
    public Page<PortfolioCard> getMyPortfolios(PortfolioSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(request.sort().stream()
                        .map(s -> s.direction().equalsIgnoreCase("DESC")
                                ? Sort.Order.desc(s.property())
                                : Sort.Order.asc(s.property()))
                        .toList())
        );

        return portfolioQueryRepositoryImpl.findMyPortfolios(request.keyword(), request.tags(), pageable);
    }

    // 내가 좋아요 표시한 글 목록
    @Transactional
    public Page<PortfolioCard> getMyLikedPortfolios(PortfolioSearchRequest request){
        Long userId = authenticatedUserProvider.getAuthenticatedUser().getId();
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(request.sort().stream()
                        .map(s -> s.direction().equalsIgnoreCase("DESC")
                                ? Sort.Order.desc(s.property())
                                : Sort.Order.asc(s.property()))
                        .toList())
        );

        return portfolioQueryRepositoryImpl.findLikedPortfoliosByUserId(request.keyword(), request.tags(), userId, pageable);
    }

    // 포트폴리오 상세
    @Transactional
    public PortfolioDetailResponse getPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오가 존재하지 않습니다."));

        return PortfolioDetailResponse.of(portfolio);
    }

}