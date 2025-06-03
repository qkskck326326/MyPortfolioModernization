package co.kr.my_portfolio.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailLoginCommand implements LoginCommand {
    private String email;
    private String password;
}