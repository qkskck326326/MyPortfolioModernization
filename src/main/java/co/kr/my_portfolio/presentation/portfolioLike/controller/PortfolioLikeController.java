package co.kr.my_portfolio.presentation.portfolioLike.controller;

import co.kr.my_portfolio.application.portfolioLike.PortfolioLikeService;
import co.kr.my_portfolio.global.response.CommonResponse;
import co.kr.my_portfolio.presentation.portfolioLike.dto.PortfolioLikeToggleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PortfolioLike", description = "포트폴리오 좋아요 관련 API")
@RestController
@RequestMapping("/api/portfolioLike")
@RequiredArgsConstructor
public class PortfolioLikeController {
    private final PortfolioLikeService portfolioLikeService;

    // 포트폴리오 좋아요 표시 토글
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "포트폴리오 좋아요 토글",
            description = """
                    이미 좋아요 되어 있으면 좋아요 해제, 아니면 좋아요를 등록하는 API 입니다.
                    API 사용 조건
                    - JWT 인증(로그인)
                    - portfolioId 생략 불가, 0 이상의 숫자
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @ApiResponse(responseCode = "400", description = "portfolioId 누락 또는 유효하지 않음"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "404", description = "해당 포트폴리오가 존재하지 않음")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<String>> portfolioLikeToggle(
            @RequestBody PortfolioLikeToggleRequest request
    ){
        portfolioLikeService.portfolioLikeToggle(request.getPortfolioId());
        return null;
    }
}
