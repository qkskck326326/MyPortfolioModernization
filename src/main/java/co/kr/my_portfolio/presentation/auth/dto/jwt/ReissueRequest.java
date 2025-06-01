package co.kr.my_portfolio.presentation.auth.dto.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequest {

    @NotBlank(message = "Refresh Token 이 포함되지 않았습니다.")
    private String refreshToken;
}