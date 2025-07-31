package co.kr.my_portfolio.domain.user;

import co.kr.my_portfolio.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(nullable = false, unique = true, length = 30)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(length = 500)
    private String userThumbnail;

    @Column(length = 2000)
    private String introduce;

    @Column(length = 100)
    private String github;

    private LocalDate birth;

    @Builder
    public User(String email, String password, String nickname, String slug, Role role,
                String userThumbnail, String introduce, String github, LocalDate birth) {
        validateNickname(nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.slug = slug;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.userThumbnail = userThumbnail;
        this.introduce = introduce;
        this.github = github;
        this.birth = birth;
    }

    private User (Long userId){
        this.id = userId;
    }

    public static User withId(Long userId) {
        return new User(userId);
    }

    public void updateProfile(String nickname, String userThumbnail, String introduce,
                              String github, LocalDate birth) {
        if (nickname != null) this.nickname = nickname;
        if (userThumbnail != null) this.userThumbnail = userThumbnail;
        if (introduce != null) this.introduce = introduce;
        if (github != null) this.github = github;
        if (birth != null) this.birth = birth;
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.length() > 30) {
            throw new IllegalArgumentException("닉네임은 1자 이상 30자 이하여야 합니다.");
        }
    }

    public void updatePassword(String newPassword) {
        // TODO - 비밀번호 복잡도 검증 로직
        this.password = newPassword;
    }

    public void deactivate() {
        this.status = UserStatus.DEACTIVATED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
}
