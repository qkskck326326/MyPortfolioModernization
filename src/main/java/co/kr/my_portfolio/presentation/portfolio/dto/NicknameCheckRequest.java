package co.kr.my_portfolio.presentation.portfolio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "닉네임 중복 확인 요청")
public class NicknameCheckRequest {

    @Schema(description = "중복 여부를 확인할 닉네임", example = "건우")
    @NotBlank
    private String nickname;

    public NicknameCheckRequest() {}

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
