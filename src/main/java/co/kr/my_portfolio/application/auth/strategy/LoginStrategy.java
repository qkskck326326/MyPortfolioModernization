package co.kr.my_portfolio.application.auth.strategy;

import co.kr.my_portfolio.application.auth.dto.LoginCommand;
import co.kr.my_portfolio.domain.user.User;

public interface LoginStrategy {
    boolean supports(LoginCommand request);
    User authenticate(LoginCommand request);
}
