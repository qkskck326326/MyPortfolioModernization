package co.kr.my_portfolio.presentation.portfolio.controller;

import co.kr.my_portfolio.application.portfolio.PortfolioService;
import co.kr.my_portfolio.global.response.ApiResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> savePortfolio(@RequestBody @Valid PortfolioSaveRequest request) {
        portfolioService.savePortfolio(request);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오가 성공적으로 등록되었습니다."));
    }
    
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updatePortfolio(@RequestBody @Valid PortfolioUpdateRequest request) {
        portfolioService.updatePortfolioAndTags(request);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오가 성공적으로 수정되었습니다."));
    }
}