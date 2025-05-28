package co.kr.my_portfolio.auth.strategy;

import co.kr.my_portfolio.auth.dto.login.LoginRequest;
import co.kr.my_portfolio.user.domain.User;

public interface LoginStrategy {
    boolean supports(LoginRequest request);
    User authenticate(LoginRequest request);
}
