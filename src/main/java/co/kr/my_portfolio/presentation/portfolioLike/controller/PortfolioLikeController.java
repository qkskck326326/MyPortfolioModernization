package co.kr.my_portfolio.presentation.portfolioLike.controller;

import co.kr.my_portfolio.application.portfolioLike.PortfolioLikeService;
import co.kr.my_portfolio.global.response.CommonResponse;
import co.kr.my_portfolio.presentation.portfolioLike.dto.PortfolioLikeToggleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolioLike")
@RequiredArgsConstructor
public class PortfolioLikeController {
    private final PortfolioLikeService portfolioLikeService;

    // 포트폴리오 좋아요 표시 토글
    @PostMapping
    public ResponseEntity<CommonResponse<String>> portfolioLikeToggle(
            @RequestBody PortfolioLikeToggleRequest request
    ){
        portfolioLikeService.portfolioLikeToggle(request.getPortfolioId());
        return null;
    }
}
