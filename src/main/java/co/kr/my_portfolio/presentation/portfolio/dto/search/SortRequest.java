package co.kr.my_portfolio.presentation.portfolio.dto.search;

public record SortRequest(
        String property,     // 정렬할 필드명
        String direction     // ASC or DESC
) {}
