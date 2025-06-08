package co.kr.my_portfolio.presentation.auth.dto.login;

import co.kr.my_portfolio.application.auth.dto.EmailLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(name = "EmailLoginRequest - 이메일 로그인 요청", description = """
        이메일 + 패스워드로 로그인하는 전통적인 로그인 방식 Request 입니다.
        """)
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