package co.kr.my_portfolio.presentation.user.dto;

import co.kr.my_portfolio.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Schema(name = "UserProfileResponse - 유저 프로필 요청 응답 DTO", description = """
        유저 프로필 요청 반환 DTO 입니다.
        """)
@Getter
public class UserProfileResponse {
    @Schema(description = "닉네임", example = "이건우")
    private final String nickname;

    @Schema(description = "썸네일 링크", example = "https://example.com/image.jpg")
    private final String userThumbnail;

    @Schema(description = "깃허브 링크", example = "https://github.com/qkskck326326")
    private final String github;

    @Schema(description = "소개글", example = "새싹개발자")
    private final String introduce;

    @Schema(description = "생일", example = "1996-10-13")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate birth;

    @Builder
    public UserProfileResponse(String nickname,
                               String userThumbnail, String github,
                               String introduce, LocalDate birth) {
        this.nickname = nickname;
        this.userThumbnail = userThumbnail;
        this.github = github;
        this.introduce = introduce;
        this.birth = birth;
    }

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .nickname(user.getNickname())
                .userThumbnail(user.getUserThumbnail())
                .github(user.getGithub())
                .introduce(user.getIntroduce())
                .birth(user.getBirth())
                .build();
    }
}
