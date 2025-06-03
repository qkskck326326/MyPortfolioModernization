package co.kr.my_portfolio.presentation.portfolio.controller;

import co.kr.my_portfolio.application.portfolio.PortfolioService;
import co.kr.my_portfolio.domain.portfolio.Portfolio;
import co.kr.my_portfolio.domain.portfolio.PortfolioCard;
import co.kr.my_portfolio.global.response.ApiResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.search.PortfolioSearchRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    
    // 포트폴리오 등록
    @PostMapping
    public ResponseEntity<ApiResponse<String>> savePortfolio(@RequestBody @Valid PortfolioSaveRequest request) {
        portfolioService.savePortfolio(request);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오가 성공적으로 등록되었습니다."));
    }
    
    // 포트폴리오 수정
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updatePortfolio(@RequestBody @Valid PortfolioUpdateRequest request) {
        portfolioService.updatePortfolioAndTags(request);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오가 성공적으로 수정되었습니다."));
    }
    
    // 포트폴리오 삭제
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<String>> deletePortfolio(@PathVariable long portfolioId){
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오를 성공적으로 삭제하였습니다."));
    }

    // 포트폴리오 보기
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<PortfolioCard>>> getPortfolioCards(@RequestBody PortfolioSearchRequest request) {
        Page<PortfolioCard> portfolioCards = portfolioService.getPortfolioCards(request);
        return ResponseEntity.ok(ApiResponse.success(portfolioCards, "불러오기 성공"));
    }
}