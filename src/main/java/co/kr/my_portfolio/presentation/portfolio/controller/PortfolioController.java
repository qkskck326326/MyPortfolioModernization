package co.kr.my_portfolio.presentation.portfolio.controller;

import co.kr.my_portfolio.application.portfolio.PortfolioService;
import co.kr.my_portfolio.domain.portfolio.PortfolioCard;
import co.kr.my_portfolio.global.response.ApiResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioDetailResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.search.PortfolioSearchRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Portfolio", description = "포트폴리오 관련 API")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    // 포트폴리오 등록
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "포트폴리오 등록",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - title, content 생략 불가.
                    - thumbnail, tags 생략 가능.
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioSaveResponse>> savePortfolio(@RequestBody @Valid PortfolioSaveRequest request) {
        long portfolioId = portfolioService.savePortfolio(request);
        return ResponseEntity.ok(ApiResponse.success(PortfolioSaveResponse.of(portfolioId), "포트폴리오가 성공적으로 등록되었습니다."));
    }
    
    // 포트폴리오 수정
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "포트폴리오 수정",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - 해당 Portfolio 작성자와 동일한 아이디
                    - portfolioId, title, content 생략 불가.
                    - thumbnail, tags 생략 가능.
                    """)
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updatePortfolio(@RequestBody @Valid PortfolioUpdateRequest request) {
        portfolioService.updatePortfolioAndTags(request);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오가 성공적으로 수정되었습니다."));
    }
    
    // 포트폴리오 삭제
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "포트폴리오 삭제",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - 해당 Portfolio 작성자와 동일한 아이디
                    - portfolioId 생략 불가.
                    """)
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<String>> deletePortfolio(@PathVariable long portfolioId){
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(null, "포트폴리오를 성공적으로 삭제하였습니다."));
    }

    // 포트폴리오 검색 & 불러오기
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "포트폴리오 검색",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - keyword, tags 생략 가능
                    - page 기본값 0, size 기본값 15
                    - sort-field 기본값 createdAt
                        - sort-field = createdAt or likeCount
                    - sort-direction 기본값 DESC
                        - sort-direction = DESC or ASC
                    """)
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<PortfolioCard>>> getPortfolioCards(@RequestBody PortfolioSearchRequest request) {
        Page<PortfolioCard> portfolioCards = portfolioService.getPortfolioCards(request);
        return ResponseEntity.ok(ApiResponse.success(portfolioCards, "불러오기 성공"));
    }
    
    // 내가 쓴 포트폴리오 검색 & 불러오기
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내가 쓴 포트폴리오 검색",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - keyword, tags 생략 가능
                    - page 기본값 0, size 기본값 15
                    - sort-field 기본값 createdAt
                        - sort-field = createdAt or likeCount
                    - sort-direction 기본값 DESC
                        - sort-direction = DESC or ASC
                    """)
    @PostMapping("/search/my")
    public ResponseEntity<ApiResponse<Page<PortfolioCard>>> getMyPortfolioCards(@RequestBody PortfolioSearchRequest request) {
        Page<PortfolioCard> portfolioCards = portfolioService.getMyPortfolios(request);
        return ResponseEntity.ok(ApiResponse.success(portfolioCards, "불러오기 성공"));
    }
    
    // 내가 좋아요 표시한 포트폴리오 불러오기
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내가 좋아요 표시한 포트폴리오 검색",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - keyword, tags 생략 가능
                    - page 기본값 0, size 기본값 15
                    - sort-field 기본값 createdAt
                        - sort-field = createdAt or likeCount
                    - sort-direction 기본값 DESC
                        - sort-direction = DESC or ASC
                    """)
    @PostMapping("search/my/liked")
    public ResponseEntity<ApiResponse<Page<PortfolioCard>>> getMyLikedPortfolios(@RequestBody PortfolioSearchRequest request){
        Page<PortfolioCard> portfolioCards = portfolioService.getMyLikedPortfolios(request);
        return ResponseEntity.ok(ApiResponse.success(portfolioCards, "불러오기 성공"));
    }

    // 포트폴리오 상세
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "특정 포트폴리오 상세",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - portfolioId 생략 불가
                    """)
    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getPortfolio(@PathVariable long portfolioId) {
        PortfolioDetailResponse portfolioDetail = portfolioService.getPortfolio(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(portfolioDetail, "포트폴리오를 성공적으로 불러왔습니다."));
    }

}