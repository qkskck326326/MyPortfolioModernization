package co.kr.my_portfolio.presentation.auth.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
