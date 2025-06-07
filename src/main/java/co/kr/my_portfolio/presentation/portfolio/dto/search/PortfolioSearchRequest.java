package co.kr.my_portfolio.presentation.portfolio.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PortfolioSearchRequest {

    private String keyword;
    private List<String> tags;
    private int page;
    private int size;
    private List<SortRequest> sort = new ArrayList<>();

    public PortfolioSearchRequest(String keyword, List<String> tags, int page, int size, List<SortRequest> sort) {
        this.keyword = keyword;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.page = page;
        this.size = size;
        this.sort = sort != null ? sort : new ArrayList<>();
    }
}
