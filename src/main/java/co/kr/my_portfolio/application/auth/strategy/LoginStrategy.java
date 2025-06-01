package co.kr.my_portfolio.application.auth.strategy;

import co.kr.my_portfolio.presentation.auth.dto.login.LoginRequest;
import co.kr.my_portfolio.domain.user.User;

public interface LoginStrategy {
    boolean supports(LoginRequest request);
    User authenticate(LoginRequest request);
}
