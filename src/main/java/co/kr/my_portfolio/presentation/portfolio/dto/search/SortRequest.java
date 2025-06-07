package co.kr.my_portfolio.presentation.portfolio.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@NoArgsConstructor
public class SortRequest {
    private String field;
    private Sort.Direction direction;
}
