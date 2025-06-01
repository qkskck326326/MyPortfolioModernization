package co.kr.my_portfolio.presentation.auth.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequest {
    @NotBlank
    private String refreshToken;
}
