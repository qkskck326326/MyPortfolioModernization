package co.kr.my_portfolio.user.dto;

import co.kr.my_portfolio.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserProfileResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String userThumbnail;
    private final String github;
    private final String introduce;
    private final LocalDate birth;

    @Builder
    public UserProfileResponse(Long id, String email, String nickname,
                               String userThumbnail, String github,
                               String introduce, LocalDate birth) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.userThumbnail = userThumbnail;
        this.github = github;
        this.introduce = introduce;
        this.birth = birth;
    }

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userThumbnail(user.getUserThumbnail())
                .github(user.getGithub())
                .introduce(user.getIntroduce())
                .birth(user.getBirth())
                .build();
    }
}
