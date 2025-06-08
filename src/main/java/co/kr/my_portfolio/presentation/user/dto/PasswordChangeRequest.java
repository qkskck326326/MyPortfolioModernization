package co.kr.my_portfolio.presentation.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(name = "PasswordChangeRequest - 유저 비밀번호 수정 요청 DTO", description = """
        유저 비밀번호 수정 요청 DTO 입니다.
        """)
@Getter
public class PasswordChangeRequest {

    @Schema(description = "기존의 비밀번호", example = "@myPassword1")
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "@myPassword2")
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
