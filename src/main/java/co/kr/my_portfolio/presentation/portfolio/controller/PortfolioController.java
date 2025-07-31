package co.kr.my_portfolio.presentation.portfolio.controller;

import co.kr.my_portfolio.application.portfolio.PortfolioService;
import co.kr.my_portfolio.domain.portfolio.PortfolioCard;
import co.kr.my_portfolio.global.response.CommonResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioDetailResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioSaveResponse;
import co.kr.my_portfolio.presentation.portfolio.dto.search.PortfolioSearchRequest;
import co.kr.my_portfolio.presentation.portfolio.dto.PortfolioUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 누락 또는 Validation 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<PortfolioSaveResponse>> savePortfolio(@RequestBody @Valid PortfolioSaveRequest request) {
        long portfolioId = portfolioService.savePortfolio(request);
        return ResponseEntity.ok(CommonResponse.success(PortfolioSaveResponse.of(portfolioId), "포트폴리오가 성공적으로 등록되었습니다."));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 누락 또는 Validation 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "403", description = "작성자 아닌 유저의 요청"),
            @ApiResponse(responseCode = "404", description = "포트폴리오 없음")
    })
    @PutMapping
    public ResponseEntity<CommonResponse<String>> updatePortfolio(@RequestBody @Valid PortfolioUpdateRequest request) {
        portfolioService.updatePortfolioAndTags(request);
        return ResponseEntity.ok(CommonResponse.success(null, "포트폴리오가 성공적으로 수정되었습니다."));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "403", description = "작성자 아님"),
            @ApiResponse(responseCode = "404", description = "포트폴리오 없음")
    })
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<CommonResponse<String>> deletePortfolio(@PathVariable long portfolioId){
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.ok(CommonResponse.success(null, "포트폴리오를 성공적으로 삭제하였습니다."));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PostMapping("/search/public")
    public ResponseEntity<CommonResponse<Page<PortfolioCard>>> getPortfolioCards(@RequestBody PortfolioSearchRequest request) {
        System.out.println("request.toString() = " + request.toString());
        Page<PortfolioCard> portfolioCards = portfolioService.getPortfolioCards(request);
        System.out.println("portfolioCards = " + portfolioCards.getContent());
        return ResponseEntity.ok(CommonResponse.success(portfolioCards, "불러오기 성공"));
    }
    
    // 특정 유저가 쓴 포트폴리오 slug로 검색 & 불러오기
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "특정 유저가 쓴 포트폴리오 검색",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - keyword, tags 생략 가능
                    - slug 생략 불가능
                    - page 기본값 0, size 기본값 15
                    - sort-field 기본값 createdAt
                        - sort-field = createdAt or likeCount
                    - sort-direction 기본값 DESC
                        - sort-direction = DESC or ASC
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PostMapping("/search/{slug}/public")
    public ResponseEntity<CommonResponse<Page<PortfolioCard>>> getMyPortfolioCards(
            @PathVariable String slug,
            @RequestBody PortfolioSearchRequest request) {
        Page<PortfolioCard> portfolioCards = portfolioService.getMyPortfolios(slug, request);
        return ResponseEntity.ok(CommonResponse.success(portfolioCards, "불러오기 성공"));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패")
    })
    @PostMapping("search/my/liked")
    public ResponseEntity<CommonResponse<Page<PortfolioCard>>> getMyLikedPortfolios(@RequestBody PortfolioSearchRequest request){
        Page<PortfolioCard> portfolioCards = portfolioService.getMyLikedPortfolios(request);
        return ResponseEntity.ok(CommonResponse.success(portfolioCards, "불러오기 성공"));
    }

    // 포트폴리오 상세
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "특정 포트폴리오 상세",
            description = """
                    API 사용 조건
                    - JWT 인증( 로그인 )
                    - portfolioId 생략 불가
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 인증 실패"),
            @ApiResponse(responseCode = "404", description = "포트폴리오 없음")
    })
    @GetMapping("/{portfolioId}/public")
    public ResponseEntity<CommonResponse<PortfolioDetailResponse>> getPortfolio(@PathVariable long portfolioId) {
        PortfolioDetailResponse portfolioDetail = portfolioService.getPortfolio(portfolioId);
        return ResponseEntity.ok(CommonResponse.success(portfolioDetail, "포트폴리오를 성공적으로 불러왔습니다."));
    }

}