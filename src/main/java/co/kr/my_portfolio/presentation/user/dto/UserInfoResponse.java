package co.kr.my_portfolio.presentation.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    @Schema(description = "유저 Nickname", example = "이건우")
    private String nickname;

    @Schema(description = "유저 NickName 기반 slug", example = "@leegeonwoo-1")
    private String slug;
}
