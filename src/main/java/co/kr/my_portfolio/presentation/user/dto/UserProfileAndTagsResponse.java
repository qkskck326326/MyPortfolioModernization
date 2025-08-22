package co.kr.my_portfolio.presentation.user.dto;

import co.kr.my_portfolio.presentation.portfolio.dto.TagCountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "공개 유저 정보 + 태그 통계 응답")
public class UserProfileAndTagsResponse {
    @Schema(description = "닉네임", example = "gwonwoo")
    private String nickname;

    @Schema(description = "slug", example = "gwonwoo")
    private String slug;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/user.jpg")
    private String userThumbnail;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "GitHub URL", example = "https://github.com/gwonwoo")
    private String github;

    @Schema(description = "자기소개", example = "안녕하세요. 백엔드 개발자입니다.")
    private String introduce;

    @Schema(description = "사용한 태그 목록")
    private List<TagCountDto> tags;

    public static UserProfileAndTagsResponse from(co.kr.my_portfolio.domain.user.User user, List<TagCountDto> tags) {
        return UserProfileAndTagsResponse.builder()
                .nickname(user.getNickname())
                .slug(user.getSlug())
                .userThumbnail(user.getUserThumbnail())
                .email(user.getEmail())
                .github(user.getGithub())
                .introduce(user.getIntroduce())
                .tags(tags)
                .build();
    }
}
