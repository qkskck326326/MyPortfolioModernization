package co.kr.my_portfolio.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Schema(name = "UserProfileUpdateRequest - 유저 프로필 수정 요청 DTO", description = """
        유저 프로필 수정 요청 DTO 입니다.
        """)
@Getter
public class UserProfileUpdateRequest {

    @Schema(description = "닉네임", example = "이건우")
    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;

    @Schema(description = "썸네일 링크", example = "https://example.com/image.jpg")
    @Size(max = 500, message = "썸네일 URL은 최대 500자까지 가능합니다.")
    private String userThumbnail;

    @Schema(description = "소개글", example = "새싹개발자")
    @Size(max = 2000, message = "소개글은 최대 2000자까지 가능합니다.")
    private String introduce;

    @Schema(description = "깃허브 링크", example = "https://github.com/qkskck326326")
    @Size(max = 100, message = "GitHub 주소는 최대 100자까지 가능합니다.")
    private String github;

    @Schema(description = "생일", example = "1996-10-13")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;
}
