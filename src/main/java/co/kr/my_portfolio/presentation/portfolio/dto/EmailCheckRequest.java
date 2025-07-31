package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 중복 확인 요청")
public class EmailCheckRequest {

    @Schema(description = "중복 여부를 확인할 이메일", example = "test@example.com")
    @NotBlank
    @Email
    private String email;

    // 기본 생성자
    public EmailCheckRequest() {}

    // getter / setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
