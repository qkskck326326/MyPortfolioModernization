package co.kr.my_portfolio.presentation.auth.dto.login;

import co.kr.my_portfolio.application.auth.dto.EmailLoginCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailLoginRequest{
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public EmailLoginCommand toCommand() {
        return new EmailLoginCommand(email, password);
    }
}