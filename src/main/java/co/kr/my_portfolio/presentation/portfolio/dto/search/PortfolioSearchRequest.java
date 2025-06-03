package co.kr.my_portfolio.presentation.portfolio.dto.search;

import java.util.List;

public record PortfolioSearchRequest(
        String keyword,                // 검색 키워드 (nullable)
        List<String> tags,            // 태그 리스트 (nullable)
        int page,                     // 페이지 번호 (0부터 시작)
        int size,                     // 페이지 크기
        List<SortRequest> sort        // 정렬 기준 리스트
) {}
